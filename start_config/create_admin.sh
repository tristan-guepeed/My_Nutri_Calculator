#!/bin/bash
set -e

DB_NAME="my_nutri_calc"
DB_USER="user"
DB_PASS="password"
ADMIN_UUID="d290f1ee-6c54-4b01-90e6-d701748f0851"
ADMIN_PASSWORD_HASH=$(htpasswd -bnBC 10 "" adminpass | tr -d ':\n')

echo "🔧 Création de la base de données et de l'utilisateur MySQL..."

if ! sudo mysql -u root -p$DB_PASS <<EOF
CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASS';
GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';
FLUSH PRIVILEGES;
EOF
then
  echo "❌ Erreur lors de la création de la base de données ou de l'utilisateur MySQL."
  exit 1
fi

echo "✅ Database and user created."

echo "⚠️ Lancement de l'application Java pour création des tables (à faire manuellement ou via script séparé)"
read -p "Appuie sur ENTER une fois que l'application a démarré et créé les tables..."

echo "🔧 Insertion de l'utilisateur admin..."

if ! mysql -u $DB_USER -p$DB_PASS $DB_NAME <<EOF
INSERT INTO users (id, username, password, email, first_name, last_name, role)
VALUES (
    UNHEX(REPLACE('$ADMIN_UUID', '-', '')),
    'admin',
    '$ADMIN_PASSWORD_HASH',
    'admin@example.com',
    'Super',
    'Admin',
    'ADMIN'
);
EOF
then
  echo "❌ Erreur lors de l'insertion de l'utilisateur admin."
  exit 1
fi

echo "✅ Admin inserted with UUID $ADMIN_UUID"
