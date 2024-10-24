
CREATE TABLE IF NOT EXISTS rates_info
(
    id integer primary key generated always as identity,
    currency VARCHAR(255),
    personal_data VARCHAR(255),
    time_stamp TIMESTAMP WITH TIME ZONE,
     course DOUBLE PRECISION);