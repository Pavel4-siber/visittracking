DROP TABLE IF EXISTS visits;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;

CREATE TABLE patients (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL
);

CREATE TABLE doctors (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         first_name VARCHAR(255) NOT NULL,
                         last_name VARCHAR(255) NOT NULL,
                         time_zone VARCHAR(50)
);

CREATE TABLE visits (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        start_date_time TIMESTAMP NOT NULL,
                        end_date_time TIMESTAMP NOT NULL,
                        patient_id INT NOT NULL,
                        doctor_id INT NOT NULL,
                        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
                        FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);
