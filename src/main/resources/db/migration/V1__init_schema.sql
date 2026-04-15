-- USERS TABLE
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('FREELANCER','CLIENT','ADMIN')),
    phone       VARCHAR(20),
    created_at  TIMESTAMP DEFAULT NOW()
);

-- CONTRACTS TABLE
CREATE TABLE contracts (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    total_value     NUMERIC(12,2),
    status          VARCHAR(30) DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE','BREACHED','RESOLVED','DISPUTED')),
    client_id       BIGINT NOT NULL REFERENCES users(id),
    freelancer_id   BIGINT NOT NULL REFERENCES users(id),
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- MILESTONES TABLE
CREATE TABLE milestones (
    id              BIGSERIAL PRIMARY KEY,
    contract_id     BIGINT NOT NULL REFERENCES contracts(id) ON DELETE CASCADE,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    amount          NUMERIC(12,2),
    deadline        DATE NOT NULL,
    status          VARCHAR(20) DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','COMPLETED','BREACHED')),
    completed_at    TIMESTAMP,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- EVIDENCE TABLE
CREATE TABLE evidence (
    id              BIGSERIAL PRIMARY KEY,
    contract_id     BIGINT NOT NULL REFERENCES contracts(id) ON DELETE CASCADE,
    milestone_id    BIGINT REFERENCES milestones(id),
    uploaded_by     BIGINT NOT NULL REFERENCES users(id),
    file_name       VARCHAR(255) NOT NULL,
    s3_key          VARCHAR(500) NOT NULL,
    file_type       VARCHAR(100),
    uploaded_at     TIMESTAMP DEFAULT NOW()
);

-- NOTIFICATIONS LOG
CREATE TABLE notifications (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    contract_id     BIGINT REFERENCES contracts(id),
    type            VARCHAR(30) CHECK (type IN ('EMAIL','SMS')),
    message         TEXT,
    sent_at         TIMESTAMP DEFAULT NOW(),
    status          VARCHAR(20) DEFAULT 'SENT'
);

-- INDEXES
CREATE INDEX idx_contracts_client    ON contracts(client_id);
CREATE INDEX idx_contracts_freelancer ON contracts(freelancer_id);
CREATE INDEX idx_milestones_contract ON milestones(contract_id);
CREATE INDEX idx_milestones_status   ON milestones(status);