package com.javaproject.recipemanagementapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.javaproject.recipemanagementapp.Tables.Recipe;
import com.javaproject.recipemanagementapp.Tables.User;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DatabaseHelper
{
    //All DB functions are part of context class hence you can't use it without context in AppCompactActivity derived classes
    public static User currentUser;
    public static SQLiteDatabase recipeAppDatabase;
    public static Recipe currentEditRecipe;
    public static List<Recipe> recipeList = new ArrayList<>();


    public static void setDB(Context context)
    {
        currentUser=new User();
        currentEditRecipe=new Recipe();
        recipeList=new ArrayList<>();
        //create a database if it doesn't exist
        recipeAppDatabase = context.openOrCreateDatabase("RecipeAppDatabase", Context.MODE_PRIVATE,null);
        // create a recipe database table here
        recipeAppDatabase.execSQL("CREATE TABLE IF NOT EXISTS recipe(id INTEGER PRIMARY KEY AUTOINCREMENT, recipeName TEXT UNIQUE, " +
                "ingredients TEXT, cuisine TEXT, procedure TEXT, servings INTEGER, cookingTime INTEGER, prepTime INTEGER, allergyWarning " +
                "TEXT, tags TEXT,userID INTEGER)");
        //create the user table here
        recipeAppDatabase.execSQL("CREATE TABLE IF NOT EXISTS user(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, dateOfBirth TEXT, fullName TEXT, imagePath TEXT, RemStatus BOOLEAN)");
//
        setInitialValues(context);
    }

    public static void insertUserData(String email1,String password1, String full_name)
    {
        //insert user values into the table here
        recipeAppDatabase.execSQL("INSERT INTO user(email, password, fullName) VALUES('"+email1+"','"+password1+"', '"+full_name+"');");
        currentUser=new User();
        currentUser.email=email1;
        currentUser.password=password1;
        currentUser.fullName=full_name;
    }

//    public static void setUserByEmail (String email){
//        Cursor SetUser = recipeAppDatabase.rawQuery("SELECT * FROM user WHERE email = '"+email+"';", new String[]{});
//        setCurrentUser(SetUser);
//    }

    public static Boolean checkemail (String em2)
    {
        Cursor cursor = recipeAppDatabase.rawQuery("SELECT * FROM user WHERE email = '"+em2+"'", new String[]{});
//        cursor.moveToFirst();
        if(cursor.getCount()>0){
            setCurrentUser(cursor);
            return true;
        }
        else{
            return false;
        }
    }

//    public static Cursor getRemCursor(String email3){
//        Cursor c3 = recipeAppDatabase.rawQuery("SELECT * FROM user WHERE email = '"+email3+"';", new String[]{});
//        c3.moveToFirst();
//        return c3;
//    }

    public static Boolean checkRemStatus(){
        Cursor cursor1 = recipeAppDatabase.rawQuery("SELECT * FROM user WHERE RemStatus = true;", new String[]{});
        return (cursor1.getCount()>0);
    }
    public static String getRemEmail(){
        Cursor c1 = recipeAppDatabase.rawQuery("SELECT email FROM user WHERE RemStatus = true;", new String[]{});
        c1.moveToFirst();
        return c1.getString(0).trim();

    }

    public static void resetRemStatus(){
        recipeAppDatabase.execSQL("UPDATE user SET RemStatus = false WHERE RemStatus = true;");
    }

    public static void setRemStatus(String emailRemStatus){
        recipeAppDatabase.execSQL("UPDATE user SET RemStatus = false WHERE RemStatus = true;");
        recipeAppDatabase.execSQL("UPDATE user SET RemStatus = true WHERE email = '"+emailRemStatus+"';");
//        Cursor cursor_setRemUser = recipeAppDatabase.rawQuery("SELECT * FROM user WHERE RemStatus = true;", new String[]{});
//        setCurrentUser(cursor_setRemUser);
    }

    public static void insertRecipeData(Recipe recipe)
    {
        //insert recipe values here and call this method to insert a new recipe
        String recipeToString=recipe.toString();
        if(!getRecipeByName(recipe.recipeName).recipeName.equals(""))
        {
            recipeAppDatabase.execSQL("DELETE FROM recipe WHERE recipeName like '"+recipe.recipeName+"';");
            recipeList.remove(recipe);
        }
        recipeAppDatabase.execSQL("INSERT INTO recipe(recipeName,ingredients,cuisine, procedure, servings, cookingTime, prepTime, allergyWarning, tags, userID) VALUES("+recipeToString+");");
    }
    public static Recipe getRecipeByName(String name)
    {
        for (Recipe recipe:recipeList) {
            if(recipe.recipeName.trim().equalsIgnoreCase(name.trim()))
                return recipe;
        }
        return new Recipe();
    }

    public static Boolean checklogin(String e1, String p1){
        Cursor cursor = recipeAppDatabase.rawQuery("SELECT * FROM user WHERE email = '"+e1+"' AND password = '"+p1+"';", new String[]{});
        checkemail(e1);
        return (cursor.getCount()>0);
    }

    public static boolean findEmail(String eml){
        Cursor cursor = recipeAppDatabase.rawQuery("SELECT email FROM user WHERE email = '"+eml+"';", new String[]{});
        return (cursor.getCount()>0);
    }

    public static void setNewPassword(String eml1, String new_password){
        recipeAppDatabase.execSQL("UPDATE user SET password = '"+new_password+"' WHERE email = '"+eml1+"' ;");
    }
    public static void setNewPass_options(String eml1, String old_password, String new_password){
        recipeAppDatabase.execSQL("UPDATE user SET password = '"+new_password+"' WHERE email = '"+eml1+"' and password = '"+old_password+"' ;");
    }

    public static void deleteUser(String email){
        recipeAppDatabase.execSQL("DELETE FROM user WHERE email = '"+email+"';");
        recipeAppDatabase.execSQL("UPDATE user SET RemStatus = false WHERE RemStatus = true;");
    }

    public static void deleteRecipe(Recipe recipe){
        recipeAppDatabase.execSQL("DELETE FROM recipe WHERE id = '"+recipe.recipeID+"';");
        recipeList.remove(recipe);
    }

//    public static Cursor getUserFromEmail(String em3){
//        Cursor cursor = recipeAppDatabase.rawQuery("SELECT id, email, password FROM user WHERE email = '" + em3 + "';", new String[]{});
//        return cursor;
//    }

    public static void setCurrentUser(Cursor cursor)
    {
        cursor.moveToFirst();
        User user=new User();
        user.ID=cursor.getInt(0);
        user.email=cursor.getString(1);
        user.password=cursor.getString(2);
        currentUser=user;
    }

    private static void setInitialValues(Context context)
    {
        Cursor cursor = recipeAppDatabase.rawQuery("SELECT * FROM recipe",new String[]{});
        if(cursor.getCount()==0)
        {
//            recipeAppDatabase.execSQL("INSERT INTO user(email, password, fullName, RemStatus) VALUES('admin', 'admin', 'admin', false);");
            try {
                InputStream is = context.getAssets().open("initial_recipes.xml");

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);

                Element element=doc.getDocumentElement();
                element.normalize();

                NodeList nList = doc.getElementsByTagName("recipe");

                for (int i=0; i<nList.getLength(); i++) {
                    Node node = nList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element2 = (Element) node;
                        String recipeName=getValue("name",element2);
                        ArrayList<String> ingredients= new ArrayList<>(Arrays.asList(getValue("ingredients",element2).split("~")));
                        ArrayList<String> cuisine = new ArrayList<>(Arrays.asList(getValue("cuisine",element2).split(",")));
                        String procedure=getValue("procedure",element2);
                        int servings=Integer.parseInt(getValue("servings",element2));
                        String cooking_time=getValue("cooking_time",element2);
                        String prep_time=getValue("prep_time",element2);
                        int user_id=Integer.parseInt(getValue("user_id",element2));
                        String allergens=getValue("allergens",element2);
                        ArrayList<String> tags= new ArrayList<>(Arrays.asList(getValue("tags",element2).split(",")));
                        Recipe recipe = new Recipe(0,recipeName,ingredients,cuisine,procedure,servings,cooking_time,prep_time,user_id,allergens,tags);
                        insertRecipeData(recipe);
                    }
                }

            } catch (Exception e) {e.printStackTrace();}
        }
        }
        private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public static void getAllRecipe()
    {
        String[] columns = {"id", "recipeName", "ingredients", "cuisine", "procedure", "servings", "cookingTime", "prepTime", "userID", "allergyWarning", "tags"};
        Cursor cursor = recipeAppDatabase.query("recipe", columns, null, null, null, null, null);
        while(cursor.moveToNext()){
            String[] values =new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                values[i]=cursor.getString((int)cursor.getColumnIndex(columns[i]));
            }
            try{
                if(Integer.parseInt(values[8])==-1||Integer.parseInt(values[8])==DatabaseHelper.currentUser.ID) {
                    Recipe recipe = new Recipe(Integer.parseInt(values[0]), values[1], Recipe.StringToList(values[2], ","), Recipe.StringToList(values[3], ","), values[4], Integer.parseInt(values[5]), values[6], values[7], Integer.parseInt(values[8]), values[9], Recipe.StringToList(values[10], ","));
                    recipeList.add(recipe);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
   }