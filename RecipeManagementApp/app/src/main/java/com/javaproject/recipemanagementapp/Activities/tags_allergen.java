package com.javaproject.recipemanagementapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.javaproject.recipemanagementapp.AddBulletPoints;
import com.javaproject.recipemanagementapp.DatabaseHelper;
import com.javaproject.recipemanagementapp.R;
import com.javaproject.recipemanagementapp.Tables.Recipe;

public class tags_allergen extends AppCompatActivity {
    TextView recipeName2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_allergen);
        AddBulletPoints.setBulletPoints(findViewById(R.id.allergen_edit_text),"•");
        AddBulletPoints.setBulletPoints(findViewById(R.id.tags_edit_text),"#");
        setOnClicks();
        populateTagsAndAllergen();

    }
    void populateTagsAndAllergen()
    {
        recipeName2 = findViewById(R.id.recipe_name);
        recipeName2.setText(DatabaseHelper.currentEditRecipe.recipeName.trim());
        EditText tags=findViewById(R.id.tags_edit_text);
        EditText allergen=findViewById(R.id.allergen_edit_text);
        tags.setText(Recipe.ListtoString(DatabaseHelper.currentEditRecipe.tags).replaceAll("~","#"));
        allergen.setText(DatabaseHelper.currentEditRecipe.allergyWarnings.replaceAll("~","\n•"));
    }
    void setOnClicks()
    {
        Button done = findViewById(R.id.done_button_tags_allergen);
        done.setOnClickListener(view -> {
            saveTagsAllergen();
            DatabaseHelper.currentEditRecipe.userID=DatabaseHelper.currentUser.ID;
            DatabaseHelper.insertRecipeData(DatabaseHelper.currentEditRecipe);
            DatabaseHelper.recipeList.add(DatabaseHelper.currentEditRecipe);
            Intent intent = new Intent(this, landing_page.class);
            startActivity(intent);
        });
    }

    void saveTagsAllergen()
    {
        String allergens=((EditText)findViewById(R.id.allergen_edit_text)).getText().toString();
        allergens.replaceAll("\n•","~");
        DatabaseHelper.currentEditRecipe.allergyWarnings=allergens;
        String tags=((EditText)findViewById(R.id.tags_edit_text)).getText().toString();
        tags.replaceAll("#","~");
        DatabaseHelper.currentEditRecipe.tags= Recipe.StringToList(tags,"~");
    }
}
