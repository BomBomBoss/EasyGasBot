--preconditions onFail:WARN
CREATE OR REPLACE VIEW v$_average_price AS
SELECT
    c.date AS price_date,
    ROUND((
              AVG(COALESCE(CAST(c.type_95e_price AS NUMERIC), 0)) +
              AVG(COALESCE(CAST(n.type_95e_price AS NUMERIC), 0)) +
              AVG(COALESCE(CAST(v.type_95e_price AS NUMERIC), 0)) +
              AVG(COALESCE(CAST(vr.type_95e_price AS NUMERIC), 0))
              ) / 4, 2) AS average_of_95e,
    ROUND((
              AVG(COALESCE(CAST(c.type_98e_price AS NUMERIC), 0)) +
              AVG(COALESCE(CAST(n.type_98e_price AS NUMERIC), 0)) +
              AVG(COALESCE(CAST(v.type_98e_price AS NUMERIC), 0)) +
              AVG(COALESCE(CAST(vr.type_98e_price AS NUMERIC), 0))
              ) / 4, 2) AS average_of_98e,
    ROUND((
              AVG(COALESCE(CAST(c.type_diesel_price AS NUMERIC), 0)) +
              AVG(COALESCE(CAST(n.type_diesel_price AS NUMERIC), 0)) +
              AVG(COALESCE(CAST(v.type_diesel_price AS NUMERIC), 0)) +
              AVG(COALESCE(CAST(vr.type_diesel_price AS NUMERIC), 0))
              ) / 4, 2) AS average_of_diesel
FROM circle_history c
         LEFT JOIN neste_history n ON c.date = n.date
         LEFT JOIN viada_history v ON c.date = v.date
         LEFT JOIN virsi_history vr ON c.date = vr.date
GROUP BY c.date;