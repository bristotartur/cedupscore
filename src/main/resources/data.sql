-- EDITION

INSERT INTO
    tb_edition (status, start_date, closing_date)
VALUES
    ('ENDED', '2023-04-01', '2023-05-05'),
    ('SCHEDULED', '2024-04-01', '2024-08-09');

-- TEAMS

INSERT INTO
    tb_team (name, logo_url, is_active)
VALUES
    ('Atômica', 'ATOMICA', true),
    ('Mestres de Obras', 'MESTRES', true),
    ('Papa-Léguas', 'PAPA', true),
    ('Twister', 'TWISTER', true),
    ('Unicontti', 'UNICONTTI', true);

-- TEAM SCORES

INSERT INTO
    tb_team_score (score, team_id, tasks_won, sports_won, edition_id)
VALUES
    (2300, 1, 5, 1, 1),
    (2300, 2, 5, 0, 1),
    (2200, 3, 5, 4, 1),
    (2250, 4, 5, 2, 1),
    (2100, 5, 5, 0, 1),

    (2430, 1, 5, 5, 2),
    (2410, 2, 5, 5, 2),
    (2120, 3, 5, 5, 2),
    (1500, 4, 5, 5, 2),
    (2000, 5, 5, 5, 2);

-- PARTICIPANT

INSERT INTO
    tb_participant (name, cpf, gender, type, is_active)
VALUES
    ('CARLOS SILVA', '123.456.789-00', 'MALE', 'PARENT', true),
    ('FERNANDA COSTA', '234.567.890-12', 'FEMALE', 'TEACHER', true),
    ('JULIANA MENDES', '345.678.901-23', 'FEMALE', 'TEACHER', true),
    ('EDUARDO SANTOS', '456.789.012-34', 'MALE', 'STUDENT', true),
    ('LUCAS OLIVEIRA', '567.890.123-45', 'MALE', 'STUDENT', true),
    ('ALINE SOUZA', '678.901.234-56', 'FEMALE', 'STUDENT', true),
    ('RICARDO ALMEIDA', '789.012.345-67', 'MALE', 'STUDENT', true),
    ('PATRÍCIA LIMA', '890.123.456-78', 'FEMALE', 'STUDENT', true),
    ('FELIPE COSTA', '901.234.567-89', 'MALE', 'STUDENT', true),
    ('MARIANA ROCHA', '012.345.678-90', 'FEMALE', 'STUDENT', true),
    ('JORGE MARTINS', '123.654.789-00', 'MALE', 'PARENT', true),
    ('SIMONE ALMEIDA', '234.765.890-12', 'FEMALE', 'TEACHER', true),
    ('ANA PAULA GOMES', '345.876.901-23', 'FEMALE', 'TEACHER', true),
    ('RAFAEL SANTOS', '456.987.012-34', 'MALE', 'STUDENT', true),
    ('ISABELA FERREIRA', '567.098.123-45', 'FEMALE', 'STUDENT', true),
    ('GABRIEL LIMA', '678.109.234-56', 'MALE', 'STUDENT', true),
    ('TATIANE SILVA', '789.210.345-67', 'FEMALE', 'STUDENT_PARENT', true),
    ('LUCAS ROCHA', '890.321.456-78', 'MALE', 'STUDENT', true),
    ('BRUNA COSTA', '901.432.567-89', 'FEMALE', 'STUDENT', true),
    ('FELIPE ALMEIDA', '012.543.678-90', 'MALE', 'STUDENT', true),
    ('MARCOS PEREIRA', '123.765.890-00', 'MALE', 'PARENT', true),
    ('MARIA CLARA', '234.876.901-12', 'FEMALE', 'TEACHER_PARENT', true),
    ('JÉSSICA RONCONI', '345.987.012-23', 'FEMALE', 'TEACHER', true),
    ('JOÃO PEDRO', '456.098.123-34', 'MALE', 'STUDENT', true),
    ('LAURA SOUZA', '567.109.234-45', 'FEMALE', 'STUDENT', true),
    ('DANIEL LIMA', '678.210.345-56', 'MALE', 'STUDENT', true),
    ('NATÁLIA COSTA', '789.321.456-67', 'FEMALE', 'STUDENT', true),
    ('ANDRÉ SILVA', '890.432.567-78', 'MALE', 'STUDENT', true),
    ('CAMILA ROCHA', '901.543.678-89', 'FEMALE', 'STUDENT', true),
    ('RODRIGO FERREIRA', '012.654.789-90', 'MALE', 'STUDENT', true),
    ('RICARDO GOMES', '123.876.901-00', 'MALE', 'PARENT', true),
    ('CLÁUDIA FERREIRA', '234.987.012-12', 'FEMALE', 'TEACHER', true),
    ('BRUNA LIMA', '345.098.123-23', 'FEMALE', 'TEACHER', true),
    ('FELIPE MOTTA', '456.109.234-34', 'MALE', 'STUDENT', true),
    ('AMANDA ROCHA', '567.210.345-45', 'FEMALE', 'STUDENT', true),
    ('PEDRO SILVA', '678.321.456-56', 'MALE', 'STUDENT', true),
    ('GISELE ALMEIDA', '789.432.567-67', 'FEMALE', 'STUDENT', false),
    ('GABRIEL SOUZA', '890.543.678-78', 'MALE', 'STUDENT', true),
    ('MARINA COSTA', '901.654.789-89', 'FEMALE', 'STUDENT', true),
    ('ANDERSON LIMA', '012.765.890-90', 'MALE', 'STUDENT', true),
    ('FÁBIO SANTOS', '123.987.012-00', 'MALE', 'PARENT', true),
    ('PATRÍCIA ROCHA', '234.098.123-12', 'FEMALE', 'TEACHER', true),
    ('MARIANA OLIVEIRA', '345.109.234-23', 'FEMALE', 'TEACHER', true),
    ('THIAGO ALMEIDA', '456.210.345-34', 'MALE', 'STUDENT', true),
    ('RAÍSSA LIMA', '567.321.456-45', 'FEMALE', 'STUDENT', true),
    ('LUCAS SOUZA', '678.432.567-56', 'MALE', 'STUDENT', true),
    ('BIANCA COSTA', '789.543.678-67', 'FEMALE', 'STUDENT', false),
    ('RODRIGO PEREIRA', '890.654.789-78', 'MALE', 'STUDENT', true),
    ('JULIANA MARTINS', '901.765.890-89', 'FEMALE', 'TEACHER', true),
    ('EDUARDO FERREIRA', '012.876.901-90', 'MALE', 'STUDENT', true);

-- EDITION REGISTRATIONS

INSERT INTO
    tb_edition_registration (participant_id, team_id, edition_id, created_at)
VALUES
-- Atômica
    (1, 1, 1, '2023-04-08T13:40:03.252379'),
    (2, 1, 1, '2023-04-08T13:40:03.252379'),
    (3, 1, 1, '2023-04-08T13:40:03.252379'),
    (4, 1, 1, '2023-04-08T13:40:03.252379'),
    (5, 1, 1, '2023-04-08T13:40:03.252379'),
    (6, 1, 1, '2023-04-08T13:40:03.252379'),
    (7, 1, 1, '2023-04-08T13:40:03.252379'),
    (8, 1, 1, '2023-04-08T13:40:03.252379'),
    (9, 1, 1, '2023-04-08T13:40:03.252379'),
    (10, 1, 1, '2023-04-08T13:40:03.252379'),

-- Mestres de Obras
    (11, 2, 1, '2023-04-08T13:40:03.252379'),
    (12, 2, 1, '2023-04-08T13:40:03.252379'),
    (13, 2, 1, '2023-04-08T13:40:03.252379'),
    (14, 2, 1, '2023-04-08T13:40:03.252379'),
    (15, 2, 1, '2023-04-08T13:40:03.252379'),
    (16, 2, 1, '2023-04-08T13:40:03.252379'),
    (17, 2, 1, '2023-04-08T13:40:03.252379'),
    (18, 2, 1, '2023-04-08T13:40:03.252379'),
    (19, 2, 1, '2023-04-08T13:40:03.252379'),
    (20, 2, 1, '2023-04-08T13:40:03.252379'),

-- Papa-Léguas
    (21, 3, 1, '2023-04-08T13:40:03.252379'),
    (22, 3, 1, '2023-04-08T13:40:03.252379'),
    (23, 3, 1, '2023-04-08T13:40:03.252379'),
    (24, 3, 1, '2023-04-08T13:40:03.252379'),
    (25, 3, 1, '2023-04-08T13:40:03.252379'),
    (26, 3, 1, '2023-04-08T13:40:03.252379'),
    (27, 3, 1, '2023-04-08T13:40:03.252379'),
    (28, 3, 1, '2023-04-08T13:40:03.252379'),
    (29, 3, 1, '2023-04-08T13:40:03.252379'),
    (30, 3, 1, '2023-04-08T13:40:03.252379'),

-- Twister
    (31, 4, 1, '2023-04-08T13:40:03.252379'),
    (32, 4, 1, '2023-04-08T13:40:03.252379'),
    (33, 4, 1, '2023-04-08T13:40:03.252379'),
    (34, 4, 1, '2023-04-08T13:40:03.252379'),
    (35, 4, 1, '2023-04-08T13:40:03.252379'),
    (36, 4, 1, '2023-04-08T13:40:03.252379'),
    (37, 4, 1, '2023-04-08T13:40:03.252379'),
    (38, 4, 1, '2023-04-08T13:40:03.252379'),
    (39, 4, 1, '2023-04-08T13:40:03.252379'),
    (40, 4, 1, '2023-04-08T13:40:03.252379'),

-- Unicontti
    (41, 5, 1, '2023-04-08T13:40:03.252379'),
    (42, 5, 1, '2023-04-08T13:40:03.252379'),
    (43, 5, 1, '2023-04-08T13:40:03.252379'),
    (44, 5, 1, '2023-04-08T13:40:03.252379'),
    (45, 5, 1, '2023-04-08T13:40:03.252379'),
    (46, 5, 1, '2023-04-08T13:40:03.252379'),
    (47, 5, 1, '2023-04-08T13:40:03.252379'),
    (48, 5, 1, '2023-04-08T13:40:03.252379'),
    (49, 5, 1, '2023-04-08T13:40:03.252379'),
    (50, 5, 1, '2023-04-08T13:40:03.252379');

-- Inscrições da segunda edição

INSERT INTO
    tb_edition_registration (participant_id, team_id, edition_id, created_at)
VALUES
    (1, 1, 2, NOW()),
    (2, 1, 2, NOW()),
    (3, 1, 2, NOW()),
    (4, 1, 2, NOW()),
    (5, 1, 2, NOW()),
    (6, 1, 2, NOW()),
    (7, 1, 2, NOW()),
    (8, 1, 2, NOW()),
    (9, 1, 2, NOW()),
    (20, 1, 2, NOW()),

    (11, 2, 2, NOW()),
    (12, 2, 2, NOW()),
    (13, 2, 2, NOW()),
    (14, 2, 2, NOW()),
    (15, 2, 2, NOW()),
    (16, 2, 2, NOW()),
    (17, 2, 2, NOW()),
    (18, 2, 2, NOW()),
    (19, 2, 2, NOW()),
    (10, 2, 2, NOW()),

    (21, 3, 2, NOW()),
    (22, 3, 2, NOW()),
    (23, 3, 2, NOW()),
    (24, 3, 2, NOW()),
    (25, 3, 2, NOW()),
    (26, 3, 2, NOW()),
    (27, 3, 2, NOW()),
    (28, 3, 2, NOW()),
    (29, 3, 2, NOW()),
    (30, 3, 2, NOW()),

    (31, 4, 2, NOW()),
    (32, 4, 2, NOW()),
    (33, 4, 2, NOW()),
    (34, 4, 2, NOW()),
    (35, 4, 2, NOW()),
    (36, 4, 2, NOW()),
    (37, 4, 2, NOW()),
    (38, 4, 2, NOW()),
    (39, 4, 2, NOW()),
    (40, 4, 2, NOW()),

    (41, 5, 2, NOW()),
    (42, 5, 2, NOW()),
    (43, 5, 2, NOW()),
    (44, 5, 2, NOW()),
    (45, 5, 2, NOW()),
    (46, 5, 2, NOW()),
    (47, 5, 2, NOW()),
    (48, 5, 2, NOW()),
    (49, 5, 2, NOW()),
    (50, 5, 2, NOW());
