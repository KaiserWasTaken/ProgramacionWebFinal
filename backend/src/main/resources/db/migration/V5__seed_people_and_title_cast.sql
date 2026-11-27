RENAME TABLE title_credits TO title_cast;

INSERT INTO people (id, name, role_type) VALUES
(1, 'Elena Varela', 'ACTOR'),
(2, 'Mateo Rios', 'ACTOR'),
(3, 'Lucia Ferrer', 'ACTOR'),
(4, 'Bruno Salcedo', 'ACTOR'),
(5, 'Isla Campos', 'ACTOR'),
(6, 'Tomas Gil', 'ACTOR'),
(7, 'Nora Vidal', 'ACTOR'),
(8, 'Ciro Mendoza', 'ACTOR'),
(9, 'Mara Solano', 'ACTOR'),
(10, 'Teo Paredes', 'ACTOR'),
(11, 'Clara Neri', 'DIRECTOR'),
(12, 'Dario Mena', 'DIRECTOR'),
(13, 'Alma Reyes', 'DIRECTOR'),
(14, 'Rafael Costa', 'DIRECTOR'),
(15, 'Ines Duarte', 'DIRECTOR');

INSERT INTO title_cast (title_id, person_id, character_name) VALUES
(1, 1, 'Elena'),
(2, 2, 'Commander Vale'),
(3, 3, 'Mira'),
(4, 4, 'Inspector Sol'),
(5, 5, 'Rosa'),
(6, 6, 'Tomas'),
(7, 7, 'Nina'),
(8, 8, 'Archivist Ciro'),
(9, 9, 'Mara'),
(10, 10, 'Teo'),
(11, 11, NULL),
(12, 12, NULL),
(13, 13, NULL),
(14, 14, NULL),
(15, 15, NULL);
