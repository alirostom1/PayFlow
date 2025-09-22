CREATE TABLE subscriptions(
    id varchar(36) PRIMARY KEY,
    serviceName varchar(50) NOT NULL,
    monthly_amount decimal(10,2) NOT NULL,
    startDate timestamp NOT NULL,
    endDate timestamp,
    status ENUM("UNPAID","PAID","OVERDUE") NOT NULL,
    subscription_type ENUM("FIXED","FLEXIBLE") NOT NULL,
    monthsEngaged int DEFAULT 0
);

CREATE TABLE payments(
    id varchar(36) PRIMARY KEY,
    subscription_id varchar(36) NOT NULL,
    dueDate timestamp NOT NULL,
    paymentDate timestamp DEFAULT NULL,
    paiment_type varchar(20) NOT NULL,
    status ENUM("PAID","UNPAID","OVERDUE") NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
);