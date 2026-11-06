DELETE FROM projects
WHERE id = 13
  AND slug = 'biblioteca-feminista-13'
  AND title = 'Biblioteca feminista';

INSERT INTO projects (
    title,
    slug,
    description,
    technologies,
    image_url,
    github_url,
    demo_url,
    featured,
    display_order,
    created_at
)
VALUES
    (
        'Fundación Verisure',
        'fundacion-verisure',
        'Aplicación React para gestionar el programa de voluntariado de Fundación Verisure: catálogo de actividades, inscripciones, propuestas, cierres, entidades colaboradoras y dashboard de impacto.',
        'React, JavaScript, Vite, SCSS, React Router, Axios, Vitest',
        '',
        'https://github.com/Proyecto-Fundacion-Verisure/frontend',
        '',
        TRUE,
        5,
        NOW()
    ),
    (
        'Servido Hackathon Stellar',
        'servido-hackathon-stellar',
        'SaaS multi-tenant para bares y restaurantes con pagos en USDC sobre Stellar: pago de la cuenta desde QR o enlace, redondeo solidario a una causa en la misma transacción y cierre automático del ticket al confirmar Horizon. Incluye contrato Soroban para el pool de donaciones.',
        'TypeScript, React, Node.js, Stellar, Soroban, Rust, USDC, Docker',
        '',
        'https://github.com/hackmeridian-kosmicteam/servido-hackathon',
        'https://hackathon-servido.agustinpane.xyz',
        TRUE,
        4,
        NOW()
    ),
    (
        'Athletica Gestión de Gimnasio',
        'athletica-gestion-gimnasio',
        'Aplicación de gestión para el gimnasio Athletica: socios, membresías, clases y reservas. API REST con FastAPI y PostgreSQL, frontend React, autenticación, tests, documentación Swagger y despliegue en Render.',
        'Python, FastAPI, PostgreSQL, React, TypeScript, Docker, Pytest',
        '',
        'https://github.com/IA-P1-BCN/g2-python-crud-api',
        'https://athletica-web.onrender.com/',
        TRUE,
        3,
        NOW()
    )
ON CONFLICT (slug) DO UPDATE
SET title = EXCLUDED.title,
    description = EXCLUDED.description,
    technologies = EXCLUDED.technologies,
    image_url = EXCLUDED.image_url,
    github_url = EXCLUDED.github_url,
    demo_url = EXCLUDED.demo_url,
    featured = EXCLUDED.featured,
    display_order = EXCLUDED.display_order;
