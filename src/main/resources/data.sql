-- SEASONS
INSERT INTO season (season_id, name, podcast_id, season_number, description, thumbnail_url, image_url) VALUES
('aaaa1111-1111-1111-1111-aaaaaaaaaaaa', 'CodeTalk Season 1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1, 'First season of CodeTalk.', 'https://cdn.example.com/seasons/codetalk_s1_thumb.jpg', 'https://cdn.example.com/seasons/codetalk_s1_image.jpg'),
('bbbb1111-1111-1111-1111-bbbbbbbbbbbb', 'LaughTrack Season 1', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 1, 'First season of LaughTrack.', 'https://cdn.example.com/seasons/laughtrack_s1_thumb.jpg', 'https://cdn.example.com/seasons/laughtrack_s1_image.jpg'),
('cccc1111-1111-1111-1111-cccccccccccc', 'CrimeLens Season 1', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 1, 'First season of CrimeLens.', 'https://cdn.example.com/seasons/crimelens_s1_thumb.jpg', 'https://cdn.example.com/seasons/crimelens_s1_image.jpg');

-- SEASON–EPISODE RELATIONS
INSERT INTO season_episode_ids (season_id, episode_id) VALUES
('aaaa1111-1111-1111-1111-aaaaaaaaaaaa', 'aaaa1111-1111-1111-1111-aaaaaaaa1111'),
('aaaa1111-1111-1111-1111-aaaaaaaaaaaa', 'aaaa2222-2222-2222-2222-aaaaaaaa2222'),
('bbbb1111-1111-1111-1111-bbbbbbbbbbbb', 'bbbb1111-1111-1111-1111-bbbbbbbb1111'),
('bbbb1111-1111-1111-1111-bbbbbbbbbbbb', 'bbbb2222-2222-2222-2222-bbbbbbbb2222'),
('cccc1111-1111-1111-1111-cccccccccccc', 'cccc1111-1111-1111-1111-cccccccc1111'),
('cccc1111-1111-1111-1111-cccccccccccc', 'cccc2222-2222-2222-2222-cccccccc2222');