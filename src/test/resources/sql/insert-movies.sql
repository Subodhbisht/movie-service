INSERT INTO movie (id, title, vote_average, vote_count, release_date, revenue, runtime, backdrop_path, budget, homepage, overview, popularity, poster_path, genres, themes)
VALUES
    (1, 'The Dark Knight', 8.5, 5000, DATE '2008-07-18', 1004558444, 152, '/dk-backdrop.jpg', 185000000, 'https://example.com/dark-knight', 'Batman faces the Joker.', 120.5, '/dk-poster.jpg', ARRAY['Action', 'Drama'], ARRAY['Heroism']),
    (2, 'Inception', 8.4, 5000, DATE '2010-07-16', 836836967, 148, '/inc-backdrop.jpg', 160000000, NULL, 'A thief steals secrets through dreams.', 110.2, '/inc-poster.jpg', ARRAY['Action', 'Science Fiction'], ARRAY['Dreams']),
    (3, 'Superbad', 7.2, 1200, DATE '2007-08-17', 170812526, 113, NULL, 20000000, NULL, NULL, 45.0, NULL, ARRAY['Comedy'], ARRAY['Friendship']),
    (4, 'Drama Queen', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (5, '100% Love', 6.1, 300, DATE '2012-02-14', NULL, 105, NULL, NULL, NULL, NULL, 12.3, NULL, ARRAY['Romance', 'Drama'], ARRAY['Love']),
    (6, 'Future Movie', NULL, 10, DATE '2999-01-01', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, ARRAY['Action'], ARRAY[]::text[]),
    (7, '1000 Love Letters', 5.5, 50, DATE '2011-01-01', NULL, NULL, NULL, NULL, NULL, NULL, 3.0, NULL, ARRAY['Romance'], ARRAY['Love']);
