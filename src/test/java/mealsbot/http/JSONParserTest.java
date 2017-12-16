package mealsbot.http;

import mealsbot.data.Recipe;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSONParserTest {

    @Test
    public void test_parseRecommendationsValid() throws Exception {
        List<Recipe> correctRecs = getTestRecommendations();

        JSONParser parser = new JSONParser();
        List<Recipe> recs = parser.parseRecommendations(JsonExamples.JSON_RECIPES_RECOMMEND_VALID);
        Assert.assertTrue(recs.size() == correctRecs.size());
        for (int i = 0; i < recs.size(); i++) {
            Recipe recipe = recs.get(i);
            Recipe answer = correctRecs.get(i);
            Assert.assertNotNull(recipe.id);
            Assert.assertNotNull(recipe.title);
            Assert.assertNotNull(recipe.imgUrl);
            Assert.assertNotNull(recipe.time);
            Assert.assertNotNull(recipe.energy);

            Assert.assertEquals(answer.id, recipe.id);
            Assert.assertEquals(answer.title, recipe.title);
            Assert.assertEquals(answer.imgUrl, recipe.imgUrl);
            Assert.assertEquals(answer.title, recipe.title);
            Assert.assertEquals(answer.energy, recipe.energy);
        }
    }

    @Test(expected = JSONException.class)
    public void test_parseRecommendationsInvalid() throws Exception {
        JSONParser parser = new JSONParser();
        parser.parseRecommendations(JsonExamples.JSON_RECIPES_RECOMMEND_INVALID);
    }

    @Test(expected = JSONException.class)
    public void test_parseRecipesInvalid() throws Exception {
        JSONParser parser = new JSONParser();
        parser.parseRecipes(JsonExamples.JSON_RECIPES_ALL_INVALID);
    }

    @Test
    public void test_parseRecipesValid() throws Exception {
        JSONParser parser = new JSONParser();
        List<Recipe> recipes = parser.parseRecipes(JsonExamples.JSON_RECIPES_ALL_VALID);
        List<Recipe> correctRecs = getTestRecipes();
        Assert.assertTrue(recipes.size() == correctRecs.size());
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            Recipe answer = correctRecs.get(i);
            Assert.assertNotNull(recipe.id);
            Assert.assertNotNull(recipe.title);
            Assert.assertNotNull(recipe.imgUrl);
            Assert.assertNotNull(recipe.stages);

            Assert.assertEquals(answer.id, recipe.id);
            Assert.assertEquals(answer.title, recipe.title);
            Assert.assertEquals(answer.imgUrl, recipe.imgUrl);
            Assert.assertEquals(answer.stages.size(), recipe.stages.size());

            for (int j = 0; j < answer.stages.size(); j++) {
                Assert.assertEquals(answer.stages.get(j).imgUrl, recipe.stages.get(j).imgUrl);
                Assert.assertEquals(answer.stages.get(j).steps.size(), recipe.stages.get(j).steps.size());

                for (int k = 0; k < answer.stages.get(j).steps.size(); k++) {
                    Assert.assertEquals(answer.stages.get(j).steps.get(k), recipe.stages.get(j).steps.get(k));
                }
            }
        }
    }

    private List<Recipe> getTestRecommendations() throws ParseException {
        return Arrays.asList(new Recipe("57e2411cf36d281f21d84ac1", "Паста с шафраном и рикоттой", 35, 345, "http://andychef.ru/wp-content/uploads/2015/01/DSC05417.jpg", null),
                new Recipe("57c97837f36d2866ee36a2e8", "Каннеллони с говядиной и горгонзолой", 110, 630, "http://1.bp.blogspot.com/-wL8BOXqGaRM/TZkbVA714II/AAAAAAAACrs/3ncoHA7FQgY/s800/Cannelloni2.jpg", null),
                new Recipe("57d938d2f36d28312f944227", "Ризотто по-милански", 35, 861, "http://www.vkusnyblog.ru/wp-content/uploads/2012/05/risotto-s-krevetkami.jpg", null),
                new Recipe("57bfffcd7ebf153a17b38337", "Классическая лазанья", 185, 430, "http://40.media.tumblr.com/b84780ffbde59e65594b7569ff03f74e/tumblr_o10y59x1df1r29uexo1_500.jpg", null),
                new Recipe("57dc0628f36d2873d81b0c93", "Глазированная свинина под соусом", 165, 428, "https://intense-earth-33481.herokuapp.com/assets/recipe19/recipe19_main.jpg", null));
    }

    private List<Recipe> getTestRecipes() throws ParseException {
        return Arrays.asList(new Recipe("57bf5e6c23a24aae1483a36c", "Брускетта с томатами и моцареллой", 20, 254, "http://img.delicious.com.au/Bdbc5JHr/h506-w759-cfill/del/2015/10/capsicum-basil-and-mozzarella-bruschetta-12227-1.jpg", getStages1()),
                new Recipe("57bfe641a6bae0f91575a084", "Курица миланезе со спагетти", 45, 965, "http://pinlavie.com/system/posts/pictures/6580/BM4Vr-XO4XtAgtLF9yuqiy.jpg", getStages2()));
    }

    private List<Recipe.Stage> getStages2() {
        List<Recipe.Stage> stages = new ArrayList<>(6);
        List<String> steps11 = new ArrayList<>(2);
        steps11.add("Мелко нарежьте чеснок");
        steps11.add("Мелко нарежьте стебли базилика, предварительно отделив от них листья");
        stages.add(new Recipe.Stage(steps11, ""));

        List<String> steps12 = new ArrayList<>(2);
        steps12.add("В сковороде разогрейте оливковое масло");
        steps12.add("Добавьте чеснок и стебли базилика");
        steps12.add("Готовьте 2 минуты");
        stages.add(new Recipe.Stage(steps12, ""));

        List<String> steps13 = new ArrayList<>(3);
        steps13.add("Добавьте консервированные томаты в глубокую сковороду");
        steps13.add("Добавьте 400 мл воды");
        steps13.add("Готовьте на медленном огне 30 минут, переодически помешивая");
        stages.add(new Recipe.Stage(steps13, ""));

        List<String> steps14 = new ArrayList<>(4);
        steps14.add("Куриные грудки отбейте до толщины в 5 миллиметров");
        steps14.add("Натрите сыр Пармезан");
        steps14.add("В разные миски добавьте муку, яйца и панировочные сухари с пармезаном");
        steps14.add("Куриные грудки обмакните в таком же порядке в каждой из мисок");
        stages.add(new Recipe.Stage(steps14, ""));

        List<String> steps15 = new ArrayList<>(2);
        steps15.add("В сковороде нагрейте оливковое масло");
        steps15.add("Обжарьте курицу с каждой стороны до золотистого цвета");
        stages.add(new Recipe.Stage(steps15, ""));

        List<String> steps16 = new ArrayList<>(3);
        steps16.add("Спагетти приготовьте до состояния аль денте");
        steps16.add("Перемешайте с томатным соусом");
        steps16.add("Подавайте с хрустящей курицей, натёртым пармезаном и листьями базилика");
        stages.add(new Recipe.Stage(steps16, ""));
        return stages;
    }

    private List<Recipe.Stage> getStages1() {
        List<Recipe.Stage> stages1 = new ArrayList<>(7);
        List<String> steps11 = new ArrayList<>(2);
        steps11.add("Мелко нарежьте чеснок");
        steps11.add("Смешайте нарезанный чеснок с размягченным сливочным маслом");
        stages1.add(new Recipe.Stage(steps11, "intense-earth-33481.herokuapp.com/assets/recipe2/brusketta_stage1.jpg"));

        List<String> steps10 = new ArrayList<>(2);
        steps10.add("Нарежьте батон крупными ломтиками");
        steps10.add("Смажьте ломтики хлеба сливочной смесью");
        stages1.add(new Recipe.Stage(steps10, "intense-earth-33481.herokuapp.com/assets/recipe2/brusketta_stage2.jpg"));

        List<String> steps12 = new ArrayList<>(2);
        steps12.add("Нарежьте помидор кольцами");
        stages1.add(new Recipe.Stage(steps12, ""));

        List<String> steps13 = new ArrayList<>(2);
        steps13.add("Нарежьте моцареллу маленькими кусочками");
        steps13.add("Положите моцареллу на хлебные ломтики");
        stages1.add(new Recipe.Stage(steps13, "intense-earth-33481.herokuapp.com/assets/recipe2/brusketta_stage3.jpg"));

        List<String> steps14 = new ArrayList<>(2);
        steps14.add("Поставьте хлеб в духовку");
        steps14.add("Достаньте хлеб, когда моцарелла немного расплавилась, а хлеб покрылся золотистой корочкой");
        stages1.add(new Recipe.Stage(steps14, "intense-earth-33481.herokuapp.com/assets/recipe2/brusketta_stage4.jpg"));

        List<String> steps15 = new ArrayList<>(3);
        steps15.add("Положите кольца помидоров сверху на моцареллу");
        steps15.add("Посолите, поперчите по вкусу");
        steps15.add("Посыпьте сверху мелконарезанным базиликом");
        stages1.add(new Recipe.Stage(steps15, "intense-earth-33481.herokuapp.com/assets/recipe2/brusketta_stage5.jpg"));

        List<String> steps16 = new ArrayList<>(3);
        steps16.add("Поставьте хлеб в духовку примерно на одну минуту");
        steps16.add("Достаньте хлеб из духовки и дайте немного остыть");
        steps16.add("Брускетта готова!");
        stages1.add(new Recipe.Stage(steps16, "intense-earth-33481.herokuapp.com/assets/recipe2/brusketta_main.jpg"));

        return stages1;
    }
}