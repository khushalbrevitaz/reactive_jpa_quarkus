DECLARE
    delay_time FLOAT := (random() * (200 - 100) + 100) / 1000;
BEGIN

    -- Sleep for the 1 sec of time
    PERFORM pg_sleep(delay_time);

END;
