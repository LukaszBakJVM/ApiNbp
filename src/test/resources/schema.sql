
CREATE TABLE IF NOT EXISTS rates_info
(
    id BIGINT primary key generated always as identity,
    currency VARCHAR(255),

    time_stamp TIMESTAMP WITH TIME ZONE,
     course DOUBLE PRECISION);




insert into rates_info (currency,time_stamp,course)values ('AUD','2024-10-26 8:51:11.7563+02',2.6752);

insert into rates_info (currency,time_stamp,course)values ('THB','2024-10-25 8:17:57.687052+02',0.1192);

insert into rates_info (currency,time_stamp,course)values ('HKD','2024-10-24 8:57:43.852021+02',0.5170);