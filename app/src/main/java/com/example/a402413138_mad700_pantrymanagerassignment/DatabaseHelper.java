package com.example.a402413138_mad700_pantrymanagerassignment;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

/**
 * SQLite Database Helper class managing pantry items and pre-populated recipes.
 * Handles database creation, initial recipe seeding, unit conversions, string normalizations,
 * and recipe matching logic (Strict and Almost-There matches).
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) { 
        super(context, DB_NAME, null, DB_VERSION); 
    }

    @Override 
    public void onCreate(SQLiteDatabase db) {
        // Create pantry and recipes tables
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, ingredients_json TEXT NOT NULL, method TEXT NOT NULL)");
        
        // Seed database with initial recipe dataset
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            seedRecipes(db);
        }
    }

    /**
     * Seeds initial 20 recipes into the database during creation or upgrade.
     */
    private void seedRecipes(SQLiteDatabase db) {
        String[][] data = {
                {"Tomato Egg Scramble","tomato|1|piece;egg|2|piece;oil|5|ml","1. Chop the tomato. 2. Heat oil in a pan. 3. Add tomato and beaten eggs; cook until set."},
                {"Cheese Omelette","egg|2|piece;cheese|30|g;oil|5|ml","1. Beat eggs. 2. Heat oil. 3. Cook eggs and add cheese; fold when set."},
                {"Banana Pancakes","banana|1|piece;egg|2|piece;flour|60|g;milk|60|ml","1. Mash banana. 2. Mix with eggs, flour and milk. 3. Cook small pancakes in a lightly oiled pan."},
                {"Garlic Tomato Pasta","pasta|100|g;tomato|2|piece;garlic|1|piece;oil|10|ml","1. Cook pasta. 2. Fry chopped garlic in oil. 3. Add chopped tomato and simmer. 4. Toss with pasta."},
                {"Simple Fried Rice","rice|150|g;egg|1|piece;peas|50|g;oil|10|ml","1. Cook rice and cool it. 2. Scramble egg in oil. 3. Add peas and rice; stir-fry until hot."},
                {"Potato Egg Hash","potato|2|piece;egg|2|piece;oil|10|ml","1. Dice and boil potatoes until nearly tender. 2. Pan-fry in oil. 3. Add eggs and cook through."},
                {"Apple Oat Porridge","oats|50|g;milk|200|ml;apple|1|piece","1. Simmer oats and milk. 2. Grate or dice the apple. 3. Stir in apple and cook until soft."},
                {"Peanut Butter Banana Toast","bread|2|slice;peanut butter|30|g;banana|1|piece","1. Toast bread. 2. Spread peanut butter. 3. Slice banana over the toast."},
                {"Tuna Pasta","pasta|100|g;tuna|1|can;mayonnaise|20|g","1. Cook and drain pasta. 2. Drain tuna. 3. Mix pasta, tuna and mayonnaise."},
                {"Cheese Tomato Toast","bread|2|slice;cheese|40|g;tomato|1|piece","1. Place sliced tomato and cheese on bread. 2. Toast or grill until cheese melts."},
                {"Spinach Omelette","egg|2|piece;spinach|40|g;oil|5|ml","1. Beat eggs. 2. Wilt spinach in a pan with oil. 3. Add eggs and cook through."},
                {"Carrot Soup","carrot|2|piece;onion|1|piece;stock|500|ml","1. Chop vegetables. 2. Simmer in stock until tender. 3. Blend carefully until smooth."},
                {"Chickpea Salad","chickpeas|150|g;tomato|1|piece;cucumber|1|piece;lemon|1|piece","1. Drain chickpeas. 2. Dice tomato and cucumber. 3. Toss with lemon juice."},
                {"Yoghurt Fruit Bowl","yoghurt|150|g;banana|1|piece;apple|1|piece","1. Slice fruit. 2. Spoon yoghurt into a bowl. 3. Top with fruit."},
                {"Garlic Butter Mushrooms","mushroom|150|g;garlic|1|piece;butter|15|g","1. Slice mushrooms and garlic. 2. Melt butter. 3. Cook mushrooms and garlic until tender."},
                {"Bean Quesadilla","tortilla|2|piece;beans|100|g;cheese|40|g","1. Spread beans over a tortilla. 2. Add cheese and top with another tortilla. 3. Toast both sides."},
                {"Lemon Rice","rice|150|g;lemon|1|piece;butter|10|g","1. Cook rice. 2. Stir in butter and lemon juice. 3. Mix and serve."},
                {"Cucumber Yoghurt Salad","cucumber|1|piece;yoghurt|100|g;lemon|0.5|piece","1. Dice cucumber. 2. Mix yoghurt with lemon juice. 3. Combine and chill briefly."},
                {"Mushroom Cheese Pasta","pasta|100|g;mushroom|100|g;cheese|30|g;milk|50|ml","1. Cook pasta. 2. Sauté mushrooms. 3. Add milk and cheese. 4. Toss with pasta."},
                {"Oats Banana Cookies","oats|100|g;banana|2|piece;peanut butter|20|g","1. Mash bananas. 2. Mix with oats and peanut butter. 3. Shape cookies and bake at 180°C for 12–15 minutes."}
        };

        for (String[] r : data) {
            JSONArray arr = new JSONArray();
            for (String part : r[1].split(";")) {
                String[] p = part.split("\\|");
                JSONObject o = new JSONObject();
                try { 
                    o.put("name", p[0]); 
                    o.put("quantity", Double.parseDouble(p[1])); 
                    o.put("unit", p[2]); 
                    arr.put(o); 
                } catch (Exception ignored) {}
            }
            ContentValues cv = new ContentValues();
            cv.put("name", r[0]);
            cv.put("ingredients_json", arr.toString());
            cv.put("method", r[2]);

            int updated = db.update("recipes", cv, "name = ?", new String[]{r[0]});
            if (updated == 0) {
                db.insert("recipes", null, cv);
            }
        }
    }

    /**
     * Inserts a new pantry item or updates an existing item in SQLite.
     *
     * @param item The pantry item to insert/update
     * @return Item ID or -1 on failure
     */
    public long savePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", normalizeName(item.name));
        cv.put("quantity", item.quantity);
        cv.put("unit", normalizeUnit(item.unit));
        cv.put("expiry", item.expiry);

        if (item.id == 0) return db.insert("pantry", null, cv);
        return db.update("pantry", cv, "id=?", new String[]{String.valueOf(item.id)}) > 0 ? item.id : -1;
    }

    /**
     * Deletes a pantry item by its unique ID.
     */
    public boolean deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    /**
     * Retrieves all pantry items ordered alphabetically by name.
     */
    public List<PantryItem> getPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT id,name,quantity,unit,expiry FROM pantry ORDER BY name", null)) {
            while (c.moveToNext()) {
                items.add(new PantryItem(c.getLong(0), c.getString(1), c.getDouble(2), c.getString(3), c.getString(4)));
            }
        }
        return items;
    }

    /**
     * Retrieves all recipes stored in the database.
     */
    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT id,name,ingredients_json,method FROM recipes ORDER BY name", null)) {
            while (c.moveToNext()) {
                recipes.add(new Recipe(c.getLong(0), c.getString(1), c.getString(2), c.getString(3)));
            }
        }
        return recipes;
    }

    /**
     * Returns recipes where ALL required ingredients are fully satisfied by available pantry quantities.
     */
    public List<Recipe> getStrictMatches() {
        List<PantryItem> pantry = getPantryItems();
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : getRecipes()) {
            if (canMake(recipe, pantry)) matches.add(recipe);
        }
        return matches;
    }

    /**
     * Returns recipes that are missing EXACTLY ONE required ingredient requirement in the pantry.
     */
    public List<Recipe> getAlmostThereMatches() {
        List<PantryItem> pantry = getPantryItems();
        List<Recipe> almostThere = new ArrayList<>();

        for (Recipe recipe : getRecipes()) {
            // Recipes that can already be made belong only in strict matches
            if (canMake(recipe, pantry)) {
                continue;
            }

            try {
                JSONArray required = new JSONArray(recipe.ingredientsJson);
                int unmetCount = 0;
                String missingMessage = "";

                for (int i = 0; i < required.length(); i++) {
                    JSONObject need = required.getJSONObject(i);
                    String wantedName = need.getString("name");
                    String wantedUnit = need.getString("unit");
                    double wantedQty = need.getDouble("quantity");

                    double available = getAvailableQuantity(pantry, wantedName, wantedUnit);

                    if (available + 0.000001 < wantedQty) {
                        unmetCount++;
                        double shortage = Math.max(0, wantedQty - available);

                        if (available <= 0.000001) {
                            missingMessage = "Missing: " + need.getString("name") +
                                    " (" + formatQuantity(wantedQty) + " " + normalizeUnit(wantedUnit) + " needed)";
                        } else {
                            missingMessage = "Insufficient quantity: " + need.getString("name") +
                                    " (need " + formatQuantity(shortage) + " " + normalizeUnit(wantedUnit) + " more)";
                        }

                        // More than one unmet requirement disqualifies the recipe from Almost-There section
                        if (unmetCount > 1) {
                            break;
                        }
                    }
                }

                // Exactly one unmet requirement matches Almost-There criteria
                if (unmetCount == 1) {
                    recipe.almostThereMessage = "Almost there! " + missingMessage;
                    almostThere.add(recipe);
                }
            } catch (Exception ignored) {}
        }

        return almostThere;
    }

    /**
     * Formats numerical quantity cleanly without trailing zero decimals.
     */
    private String formatQuantity(double quantity) {
        if (Math.abs(quantity - Math.rint(quantity)) < 0.000001) {
            return String.format(Locale.US, "%.0f", quantity);
        }
        return String.format(Locale.US, "%.4f", quantity)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }

    /**
     * Checks if all required ingredients for a recipe are available in the pantry in sufficient amounts.
     */
    private boolean canMake(Recipe recipe, List<PantryItem> pantry) {
        try {
            JSONArray required = new JSONArray(recipe.ingredientsJson);
            for (int i = 0; i < required.length(); i++) {
                JSONObject need = required.getJSONObject(i);
                String wantedName = need.getString("name");
                String wantedUnit = need.getString("unit");
                double wantedQty = need.getDouble("quantity");

                double available = getAvailableQuantity(pantry, wantedName, wantedUnit);
                if (available + 0.000001 < wantedQty) return false;
            }
            return required.length() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calculates total available quantity of an ingredient in the pantry, converting compatible units if needed.
     */
    private double getAvailableQuantity(List<PantryItem> pantry, String wantedName, String wantedUnit) {
        double total = 0;
        String normWantedName = normalizeName(wantedName);
        String normWantedUnit = normalizeUnit(wantedUnit);

        for (PantryItem item : pantry) {
            if (normalizeName(item.name).equals(normWantedName)) {
                String normItemUnit = normalizeUnit(item.unit);
                if (normItemUnit.equals(normWantedUnit)) {
                    total += item.quantity;
                } else {
                    total += getConvertedQuantity(item.quantity, normItemUnit, normWantedUnit);
                }
            }
        }
        return total;
    }

    /**
     * Converts a quantity between two units if compatible (e.g. grams to kilograms, ml to liters).
     */
    public static double getConvertedQuantity(double qty, String fromUnit, String toUnit) {
        String from = normalizeUnit(fromUnit);
        String to = normalizeUnit(toUnit);
        if (from.equals(to)) return qty;

        Double fromG = toGrams(qty, from);
        Double toG = toGrams(1.0, to);
        if (fromG != null && toG != null && toG > 0) {
            return fromG / toG;
        }

        Double fromMl = toMl(qty, from);
        Double toMl = toMl(1.0, to);
        if (fromMl != null && toMl != null && toMl > 0) {
            return fromMl / toMl;
        }

        return 0;
    }

    /**
     * Converts a weight measurement unit to grams.
     */
    private static Double toGrams(double qty, String unit) {
        switch (unit) {
            case "g": return qty;
            case "kg": return qty * 1000.0;
            case "oz": return qty * 28.3495;
            case "lb": return qty * 453.592;
            default: return null;
        }
    }

    /**
     * Converts a volume measurement unit to milliliters.
     */
    private static Double toMl(double qty, String unit) {
        switch (unit) {
            case "ml": return qty;
            case "l": return qty * 1000.0;
            case "fl oz": return qty * 29.5735;
            case "cup": return qty * 240.0;
            case "tbsp": return qty * 15.0;
            case "tsp": return qty * 5.0;
            default: return null;
        }
    }

    /**
     * Normalizes ingredient names for reliable comparison (trims, converts to lowercase, handles plurals).
     */
    public static String normalizeName(String name) {
        String n = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
        if (n.endsWith("ies") && n.length() > 4) n = n.substring(0, n.length() - 3) + "y";
        else if (n.endsWith("oes") && n.length() > 4) n = n.substring(0, n.length() - 2);
        else if (n.endsWith("s") && !n.endsWith("ss") && n.length() > 3) n = n.substring(0, n.length() - 1);
        if (n.equals("tomatoe")) n = "tomato";
        if (n.equals("mushrooms")) n = "mushroom";
        return n;
    }

    /**
     * Normalizes unit strings (e.g. "pcs", "pieces" -> "piece"; "grams" -> "g").
     */
    public static String normalizeUnit(String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        if (u.equals("pcs") || u.equals("pieces") || u.equals("pc")) return "piece";
        if (u.equals("grams") || u.equals("gram")) return "g";
        if (u.equals("kilograms") || u.equals("kilogram") || u.equals("kgs")) return "kg";
        if (u.equals("millilitres") || u.equals("milliliters") || u.equals("milliliter")) return "ml";
        if (u.equals("litres") || u.equals("liters") || u.equals("liter")) return "l";
        if (u.equals("slices")) return "slice";
        if (u.equals("ounce") || u.equals("ounces")) return "oz";
        if (u.equals("pound") || u.equals("pounds") || u.equals("lbs")) return "lb";
        if (u.equals("fluid ounce") || u.equals("fluid ounces") || u.equals("fl. oz") || u.equals("fl oz")) return "fl oz";
        if (u.equals("cups")) return "cup";
        if (u.equals("tablespoon") || u.equals("tablespoons") || u.equals("tbsp.")) return "tbsp";
        if (u.equals("teaspoon") || u.equals("teaspoons") || u.equals("tsp.")) return "tsp";
        return u;
    }
}
