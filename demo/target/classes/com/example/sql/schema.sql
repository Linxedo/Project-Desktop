-- =====================================================
-- SISTEM INFORMASI WISATA — Database Schema (PostgreSQL)
-- =====================================================
-- Jalankan skrip ini di pgAdmin atau psql setelah membuat database "wisata_db"
-- CREATE DATABASE wisata_db;

-- =====================================================
-- 1. TABEL KATEGORI
-- =====================================================
CREATE TABLE IF NOT EXISTS kategori (
    id SERIAL PRIMARY KEY,
    nama_kategori VARCHAR(100) NOT NULL UNIQUE
);

-- =====================================================
-- 2. TABEL USERS
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'Wisatawan'
        CHECK (role IN ('Admin', 'Pengelola', 'Wisatawan')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 3. TABEL DESTINASI
-- =====================================================
CREATE TABLE IF NOT EXISTS destinasi (
    id SERIAL PRIMARY KEY,
    nama_wisata VARCHAR(200) NOT NULL,
    kategori_id INTEGER REFERENCES kategori(id) ON DELETE SET NULL,
    alamat TEXT,
    deskripsi TEXT,
    harga_tiket NUMERIC(12,2) DEFAULT 0,
    jam_operasional VARCHAR(100),
    rating_rata_rata NUMERIC(3,2) DEFAULT 0,
    image_path VARCHAR(500),
    pengelola_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 4. TABEL BOOKING
-- =====================================================
CREATE TABLE IF NOT EXISTS booking (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    destinasi_id INTEGER NOT NULL REFERENCES destinasi(id) ON DELETE CASCADE,
    tgl_kunjungan DATE NOT NULL,
    jumlah_tiket INTEGER NOT NULL CHECK (jumlah_tiket > 0),
    total_harga NUMERIC(15,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending'
        CHECK (status IN ('Pending', 'Terverifikasi', 'Ditolak')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 5. TABEL ITINERARY
-- =====================================================
CREATE TABLE IF NOT EXISTS itinerary (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    nama_rencana VARCHAR(200) NOT NULL,
    tgl_rencana DATE NOT NULL,
    total_estimasi_biaya NUMERIC(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 6. TABEL ITINERARY_DETAIL
-- =====================================================
CREATE TABLE IF NOT EXISTS itinerary_detail (
    id SERIAL PRIMARY KEY,
    itinerary_id INTEGER NOT NULL REFERENCES itinerary(id) ON DELETE CASCADE,
    destinasi_id INTEGER NOT NULL REFERENCES destinasi(id) ON DELETE CASCADE,
    waktu_kunjungan TIME,
    urutan INTEGER DEFAULT 1
);

-- =====================================================
-- 7. TABEL ULASAN
-- =====================================================
CREATE TABLE IF NOT EXISTS ulasan (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    destinasi_id INTEGER NOT NULL REFERENCES destinasi(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    komentar TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, destinasi_id)
);

-- =====================================================
-- 8. TABEL WISHLIST
-- =====================================================
CREATE TABLE IF NOT EXISTS wishlist (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    destinasi_id INTEGER NOT NULL REFERENCES destinasi(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, destinasi_id)
);

-- =====================================================
-- INDEXES
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_destinasi_kategori ON destinasi(kategori_id);
CREATE INDEX IF NOT EXISTS idx_destinasi_pengelola ON destinasi(pengelola_id);
CREATE INDEX IF NOT EXISTS idx_booking_user ON booking(user_id);
CREATE INDEX IF NOT EXISTS idx_booking_destinasi ON booking(destinasi_id);
CREATE INDEX IF NOT EXISTS idx_booking_status ON booking(status);
CREATE INDEX IF NOT EXISTS idx_itinerary_user ON itinerary(user_id);
CREATE INDEX IF NOT EXISTS idx_ulasan_destinasi ON ulasan(destinasi_id);
CREATE INDEX IF NOT EXISTS idx_wishlist_user ON wishlist(user_id);

-- =====================================================
-- TRIGGER: Auto-update rating_rata_rata di destinasi
-- =====================================================
CREATE OR REPLACE FUNCTION update_rating_rata_rata()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        UPDATE destinasi
        SET rating_rata_rata = COALESCE(
            (SELECT AVG(rating)::NUMERIC(3,2) FROM ulasan WHERE destinasi_id = OLD.destinasi_id), 0
        )
        WHERE id = OLD.destinasi_id;
        RETURN OLD;
    ELSE
        UPDATE destinasi
        SET rating_rata_rata = COALESCE(
            (SELECT AVG(rating)::NUMERIC(3,2) FROM ulasan WHERE destinasi_id = NEW.destinasi_id), 0
        )
        WHERE id = NEW.destinasi_id;
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_rating ON ulasan;
CREATE TRIGGER trg_update_rating
AFTER INSERT OR UPDATE OR DELETE ON ulasan
FOR EACH ROW EXECUTE FUNCTION update_rating_rata_rata();

-- =====================================================
-- TRIGGER: Auto-update total_estimasi_biaya di itinerary
-- =====================================================
CREATE OR REPLACE FUNCTION update_estimasi_biaya()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        UPDATE itinerary
        SET total_estimasi_biaya = COALESCE(
            (SELECT SUM(d.harga_tiket) FROM itinerary_detail id2
             JOIN destinasi d ON d.id = id2.destinasi_id
             WHERE id2.itinerary_id = OLD.itinerary_id), 0
        )
        WHERE id = OLD.itinerary_id;
        RETURN OLD;
    ELSE
        UPDATE itinerary
        SET total_estimasi_biaya = COALESCE(
            (SELECT SUM(d.harga_tiket) FROM itinerary_detail id2
             JOIN destinasi d ON d.id = id2.destinasi_id
             WHERE id2.itinerary_id = NEW.itinerary_id), 0
        )
        WHERE id = NEW.itinerary_id;
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_biaya ON itinerary_detail;
CREATE TRIGGER trg_update_biaya
AFTER INSERT OR UPDATE OR DELETE ON itinerary_detail
FOR EACH ROW EXECUTE FUNCTION update_estimasi_biaya();

-- =====================================================
-- SEED DATA
-- =====================================================

-- Kategori default
INSERT INTO kategori (nama_kategori) VALUES
    ('Pantai'),
    ('Gunung'),
    ('Kuliner'),
    ('Budaya'),
    ('Taman Hiburan'),
    ('Air Terjun'),
    ('Sejarah')
ON CONFLICT (nama_kategori) DO NOTHING;

-- Admin default (password: admin123 — SHA-256 hash)
INSERT INTO users (username, password, email, role) VALUES
    ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@wisata.com', 'Admin')
ON CONFLICT (username) DO NOTHING;

-- Pengelola demo (password: pengelola123)
INSERT INTO users (username, password, email, role) VALUES
    ('pengelola1', 'b5a4cbf23e0596d1be7424e8e81ddbabfe29b4b6e3c6a0781a581d1b8b2dd07f', 'pengelola1@wisata.com', 'Pengelola')
ON CONFLICT (username) DO NOTHING;

-- Wisatawan demo (password: wisatawan123)
INSERT INTO users (username, password, email, role) VALUES
    ('wisatawan1', '7ce14a693de753bfcf23fdd0e0c4cc1e5bc2701809cfba423e027ef02ed6b8e6', 'wisatawan1@wisata.com', 'Wisatawan')
ON CONFLICT (username) DO NOTHING;

-- Destinasi demo
INSERT INTO destinasi (nama_wisata, kategori_id, alamat, deskripsi, harga_tiket, jam_operasional, image_path, pengelola_id) VALUES
    ('Pantai Kuta', 1, 'Kuta, Badung, Bali', 'Pantai ikonik di Bali dengan pasir putih dan ombak yang cocok untuk berselancar. Pemandangan sunset yang memukau menjadi daya tarik utama.', 25000, '06:00 - 18:00', 'pantai_kuta.jpg', 2),
    ('Gunung Bromo', 2, 'Probolinggo, Jawa Timur', 'Gunung berapi aktif yang terkenal dengan pemandangan sunrise spektakuler dari puncaknya. Lautan pasir yang luas menambah keindahan.', 50000, '04:00 - 17:00', 'gunung_bromo.jpg', 2),
    ('Malioboro', 4, 'Jl. Malioboro, Yogyakarta', 'Jalan legendaris di pusat kota Yogyakarta yang menjadi ikon wisata belanja dan kuliner. Penuh dengan pedagang kaki lima dan kerajinan.', 0, '24 Jam', 'malioboro.jpg', 2),
    ('Tanah Lot', 4, 'Tabanan, Bali', 'Pura yang berdiri di atas batu karang besar di tepi laut. Salah satu tempat wisata paling fotogenik di Bali.', 60000, '07:00 - 19:00', 'tanah_lot.jpg', 2),
    ('Kawah Ijen', 2, 'Banyuwangi, Jawa Timur', 'Kawah vulkanik dengan fenomena api biru (blue fire) yang menakjubkan. Danau kawah berwarna tosca yang sangat indah.', 100000, '01:00 - 12:00', 'kawah_ijen.jpg', 2),
    ('Borobudur', 7, 'Magelang, Jawa Tengah', 'Candi Buddha terbesar di dunia dan situs warisan dunia UNESCO. Keajaiban arsitektur abad ke-9 yang memukau.', 75000, '06:00 - 17:00', 'borobudur.jpg', 2),
    ('Nusa Penida', 1, 'Klungkung, Bali', 'Pulau eksotis dengan tebing-tebing dramatis dan pantai tersembunyi. Kelingking Beach menjadi spot foto paling terkenal.', 35000, '08:00 - 17:00', 'nusa_penida.jpg', 2),
    ('Taman Mini Indonesia Indah', 5, 'Jakarta Timur, DKI Jakarta', 'Taman rekreasi bertema budaya Indonesia yang menampilkan rumah adat dari seluruh provinsi. Wahana dan museum yang edukatif.', 20000, '07:00 - 22:00', 'tmii.jpg', 2)
ON CONFLICT DO NOTHING;

-- Ulasan demo
INSERT INTO ulasan (user_id, destinasi_id, rating, komentar) VALUES
    (3, 1, 5, 'Pantai yang sangat indah! Sunset-nya luar biasa. Wajib dikunjungi!'),
    (3, 2, 4, 'Pemandangan sunrise dari Bromo sangat menakjubkan. Perjalanannya cukup menantang tapi worth it.'),
    (3, 6, 5, 'Candi yang sangat megah dan bersejarah. Suasananya damai dan menginspirasi.')
ON CONFLICT (user_id, destinasi_id) DO NOTHING;
