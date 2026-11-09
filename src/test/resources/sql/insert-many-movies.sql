INSERT INTO movie (id, title, vote_count, release_date)
SELECT g, 'Movie ' || g, g, DATE '2000-01-01' + g
FROM generate_series(1001, 1120) AS g;
