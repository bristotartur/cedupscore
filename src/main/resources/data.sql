-- EDITION

INSERT INTO
    tb_edition (status, start_date, closing_date)
VALUES
    ('ENDED', '2024-04-01', '2024-08-09');

-- TEAMS

INSERT INTO
    tb_team (name, logo, is_active)
VALUES
    ('Atômica', 'ATOMICA', true),
    ('Mestres de Obras', 'MESTRES', true),
    ('Papa-Léguas', 'PAPA', true),
    ('Twister', 'TWISTER', true),
    ('Unicontti', 'UNICONTTI', true);

-- TEAM SCORES

INSERT INTO
    tb_team_score (score, team_id, edition_id)
VALUES
    (2430, 1, 1),
    (2410, 2, 1),
    (2120, 3, 1),
    (1500, 4, 1),
    (2000, 5, 1);
