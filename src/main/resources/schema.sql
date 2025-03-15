-- 1. 사용자 최대 포인트 제한 테이블
CREATE TABLE IF NOT EXISTS user_point_limit (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                user_id BIGINT NOT NULL UNIQUE,
                                                max_point_limit INT NOT NULL,
                                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 포인트 테이블
CREATE TABLE IF NOT EXISTS point (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     user_id BIGINT NOT NULL,
                                     amount INT NOT NULL,
                                     is_manual BOOLEAN NOT NULL,
                                     expiration_date TIMESTAMP NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. 포인트 트랜잭션 테이블
CREATE TABLE IF NOT EXISTS point_transaction (
                                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 point_id BIGINT NOT NULL,
                                                 used_amount INT NOT NULL,
                                                 order_id BIGINT NOT NULL,
                                                 transaction_type VARCHAR(20) NOT NULL,
    original_transaction_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );
