package com.example.a402413138_mad700_pantrymanagerassignment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_recipe_detail);
        long id=getIntent().getLongExtra("recipe_id",-1);
        DatabaseHelper helper=new DatabaseHelper(this);SQLiteDatabase db=helper.getReadableDatabase();
        try(Cursor c=db.rawQuery("SELECT name,ingredients_json,method FROM recipes WHERE id=?",new String[]{String.valueOf(id)})){
            if(c.moveToFirst()){
                ((TextView)findViewById(R.id.txtRecipeTitle)).setText(c.getString(0));
                JSONArray arr=new JSONArray(c.getString(1));StringBuilder ingredients=new StringBuilder();
                for(int i=0;i<arr.length();i++){JSONObject o=arr.getJSONObject(i);ingredients.append("• ").append(o.getString("name")).append(" — ").append(o.getDouble("quantity")).append(" ").append(o.getString("unit")).append("\n");}
                ((TextView)findViewById(R.id.txtRecipeRequired)).setText(ingredients.toString());
                ((TextView)findViewById(R.id.txtRecipeMethod)).setText(c.getString(2));
            }
        }catch(Exception ex){((TextView)findViewById(R.id.txtRecipeTitle)).setText("Recipe could not be loaded");}
    }
}
