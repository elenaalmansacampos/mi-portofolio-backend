ALTER TABLE projects ADD COLUMN IF NOT EXISTS slug VARCHAR(160);
ALTER TABLE projects ADD COLUMN IF NOT EXISTS featured BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE projects ADD COLUMN IF NOT EXISTS display_order INTEGER NOT NULL DEFAULT 0;

UPDATE projects
SET slug = COALESCE(
        NULLIF(
            trim(
                both '-'
                from lower(regexp_replace(title, '[^a-zA-Z0-9]+', '-', 'g'))
            ),
            ''
        ),
        'proyecto'
    ) || '-' || id
WHERE slug IS NULL OR slug = '';

UPDATE projects
SET display_order = id
WHERE display_order = 0;

UPDATE projects
SET featured = TRUE
WHERE featured = FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS uk_projects_slug ON projects(slug);

CREATE INDEX IF NOT EXISTS idx_projects_featured_order
    ON projects(featured, display_order);
