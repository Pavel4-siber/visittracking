INSERT INTO doctors (first_name, last_name, time_zone)
VALUES ('Dr. Emily', 'White', 'America/New_York'),
       ('Dr. John', 'Doe', 'Europe/London'),
       ('Dr. Lev', 'Jn', 'Europe/Moscow');

INSERT INTO patients (first_name, last_name)
VALUES ('Alice', 'Smith'),
       ('Bob', 'Johnson'),
       ('Charlie', 'Brown');


INSERT INTO visits (start_date_time, end_date_time, patient_id, doctor_id)
VALUES ('2025-09-20 10:00:00', '2025-09-20 11:00:00', 1, 1);


INSERT INTO visits (start_date_time, end_date_time, patient_id, doctor_id)
VALUES ('2025-10-25 15:00:00', '2025-10-25 16:00:00', 2, 2);


INSERT INTO visits (start_date_time, end_date_time, patient_id, doctor_id)
VALUES ('2025-05-03 11:00:00', '2025-05-03 12:00:00', 3, 3);
