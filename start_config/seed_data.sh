#!/bin/bash

API_URL="http://localhost:8080/api"
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="adminpass"
ADMIN_UUID="d290f1ee-6c54-4b01-90e6-d701748f0851"

echo "🔐 Logging in as admin..."
TOKEN=$(curl -s -X POST "$API_URL/users/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$ADMIN_USERNAME\", \"password\": \"$ADMIN_PASSWORD\"}" | jq -r '.token')

if [[ -z "$TOKEN" || "$TOKEN" == "null" ]]; then
  echo "❌ Login failed."
  exit 1
fi

echo "✅ Token retrieved."

create_food() {
  local name=$1
  local proteins=$2
  local carbs=$3
  local fats=$4
  local calories=$5

  response=$(curl -s -X POST "$API_URL/foods/create" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
          \"name\": \"$name\",
          \"ni\": {
            \"proteins\": $proteins,
            \"carbs\": $carbs,
            \"fats\": $fats,
            \"calories\": $calories
          },
          \"createdBy\": {
            \"id\": \"$ADMIN_UUID\"
          }
        }")
  
  echo "$response" | jq -r '.id'
}

declare -A foods=(
  ["Riz blanc"]="2.7 28 0.3 130"
  ["Poulet"]="31 0 3.6 165"
  ["Pomme"]="0.3 14 0.2 52"
  ["Banane"]="1.1 23 0.3 89"
  ["Fromage"]="25 1.3 33 402"
  ["Pain complet"]="8.5 42 2.5 250"
  ["Oeuf"]="13 1 10 143"
  ["Yaourt nature"]="4 6 3 60"
  ["Lentilles cuites"]="9 20 0.4 116"
  ["Steak haché 5%"]="26 0 5 160"
  ["Haricots verts"]="1.8 7 0.2 35"
  ["Carottes râpées"]="0.9 10 0.2 41"
  ["Pâtes cuites"]="5 25 1.1 131"
  ["Saumon"]="20 0 13 208"
  ["Courgettes"]="1.2 3.1 0.3 17"
  ["Tomates"]="0.9 3.9 0.2 18"
  ["Avocat"]="2 9 15 160"
  ["Quinoa cuit"]="4.4 21.3 1.9 120"
  ["Tofu"]="12 1.9 7 110"
  ["Poivrons"]="1 6 0.3 29"
)

declare -A food_ids

echo "🍽 Creating foods..."
for name in "${!foods[@]}"; do
  values=(${foods[$name]})
  id=$(create_food "$name" "${values[0]}" "${values[1]}" "${values[2]}" "${values[3]}")
  food_ids["$name"]=$id
  echo "✔ $name (id: $id)"
done

create_meal() {
  local name=$1
  local items=$2

  echo "🍛 Creating meal: $name"
  curl -s -X POST "$API_URL/meals/create" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"name\": \"$name\",
      \"createdBy\": { \"id\": \"$ADMIN_UUID\" },
      \"mealItems\": $items,
      \"imageData\": \"\",
      \"imageType\": \"\"
    }" | jq .
}

declare -A meals
meals["Déjeuner poulet riz"]="Poulet 150,Riz blanc 100,Haricots verts 100"
meals["Petit dej complet"]="Pain complet 60,Oeuf 100,Yaourt nature 125"
meals["Salade avocat quinoa"]="Avocat 50,Quinoa cuit 100,Tomates 80,Poivrons 50"
meals["Dîner léger"]="Lentilles cuites 150,Carottes râpées 80,Fromage 30"
meals["Pâtes saumon"]="Pâtes cuites 120,Saumon 100,Courgettes 100"
meals["Tofu quinoa légumes"]="Tofu 100,Quinoa cuit 100,Poivrons 60,Courgettes 80"
meals["Collation fruits"]="Banane 100,Pomme 100"

echo "🥗 Creating meals..."
for meal_name in "${!meals[@]}"; do
  IFS=',' read -ra items <<< "${meals[$meal_name]}"
  meal_items_json="["

  for item in "${items[@]}"; do
    food=$(echo "$item" | sed -E 's/ [0-9]+$//')
    qty=$(echo "$item" | grep -oE '[0-9]+$')
    id=${food_ids["$food"]}

    if [[ -n "$id" ]]; then
      meal_items_json+="
        { \"food\": { \"id\": $id }, \"quantity\": $qty },"
    else
      echo "⚠️ Skipping unknown food: $food"
    fi
  done

  meal_items_json="${meal_items_json%,}]"
  create_meal "$meal_name" "$meal_items_json"
done

echo "🎉 All foods and meals created successfully!"
