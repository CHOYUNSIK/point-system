-- 1. 사용자별 최대 보유 포인트 설정 (일부 사용자 제한 없음)
INSERT INTO user_point_limit (user_id, max_point_limit, created_at, updated_at)
VALUES (1, 500000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 100000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 300000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
--  사용자 4는 보유 제한이 없음

--  2. 포인트 적립 데이터 (관리자 수기 지급 포함)
INSERT INTO point (user_id, amount, is_manual, expiration_date, created_at, updated_at)
VALUES
    --  사용자 1
    (1, 1000, TRUE, DATEADD('DAY', 365, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 500, FALSE, DATEADD('DAY', 180, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    --  사용자 2
    (2, 2000, TRUE, DATEADD('DAY', 90, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 5000, FALSE, DATEADD('DAY', 365, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    --  사용자 3
    (3, 1500, FALSE, DATEADD('DAY', 200, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    --  사용자 4 (보유 제한 없음)
    (4, 3000, TRUE, DATEADD('DAY', 100, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--  3. 포인트 사용 데이터 (주문번호 포함)
INSERT INTO point_transaction (point_id, used_amount, order_id, transaction_type, created_at, updated_at)
VALUES
    (1, 800, 1234, 'USE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 300, 1234, 'USE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 1500, 5678, 'USE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 1000, 7890, 'USE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 1500, 9876, 'USE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--  4. 포인트 사용 취소 데이터 (일부 취소)
INSERT INTO point_transaction (point_id, used_amount, order_id, transaction_type, original_transaction_id, created_at, updated_at)
VALUES
    (1, 500, 1234, 'CANCEL', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 500, 7890, 'CANCEL', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--  5. 만료된 포인트 생성 (사용자 1, 2)
INSERT INTO point (user_id, amount, is_manual, expiration_date, created_at, updated_at)
VALUES
    (1, 1000, FALSE, DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 500, FALSE, DATEADD('DAY', -3, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (10, 500, FALSE, DATEADD('DAY', -3, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO point_transaction (point_id, used_amount, order_id, transaction_type, created_at, updated_at)
VALUES
    (7, 350, 1234, 'USE', DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP));

--  포인트 적립 전체 사용
INSERT INTO point (user_id, amount, is_manual, expiration_date, created_at, updated_at)
VALUES
    (11, 3000, FALSE, DATEADD('DAY', 100, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO point_transaction (point_id, used_amount, order_id, transaction_type, created_at, updated_at)
VALUES
    (10, 3000, 4567, 'USE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

