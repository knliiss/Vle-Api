-- Lowercase existing usernames and create a unique index on lower(username)
-- This migration attempts to merge duplicates by keeping the smallest id for each lowercase username.

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'users') THEN
        EXECUTE $sql$
            -- create temporary table with keeper ids per lowercase username
            CREATE TABLE IF NOT EXISTS username_normalization_tmp AS
            SELECT MIN(id) AS keep_id, LOWER(username) AS lc_username
            FROM users
            GROUP BY LOWER(username);

            -- mark duplicates by setting a temporary unique name
            UPDATE users
            SET username = ('normalized_dup_' || id)
            WHERE id IN (
                SELECT u.id
                FROM users u
                LEFT JOIN username_normalization_tmp t ON LOWER(u.username) = t.lc_username
                WHERE u.id <> t.keep_id
            );

            -- lowercase the rest
            UPDATE users SET username = LOWER(username);

            -- create unique index on lower(username)
            CREATE UNIQUE INDEX IF NOT EXISTS ux_users_username_lower ON users (LOWER(username));

            -- rename temporary duplicates to username_<id> for manual review
            UPDATE users
            SET username = ('username_' || id)
            WHERE username LIKE 'normalized_dup_%';

            DROP TABLE IF EXISTS username_normalization_tmp;
        $sql$;
    END IF;
END$$;
