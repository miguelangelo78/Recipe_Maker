package application;
import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import com.org.recipemaker.Recipe_Maker;

import np.com.ngopal.control.*;

public class Controller implements javafx.fxml.Initializable{
	private Recipe_Maker recipe;
	@FXML TextField ingredient_choice;
	@FXML AnchorPane anchorpane;
	@FXML ScrollPane scrollpane;
	@FXML Button add_ingredientBtn;
	@FXML AnchorPane ingredient_list;
	@FXML BorderPane display_content;
	@FXML Button build_recipes;
	@FXML Button clear_ingredients;
	@SuppressWarnings("rawtypes")
	public AutoFillTextBox search_ingredient;
	@SuppressWarnings("rawtypes")
	ObservableList ingredient_factory;
	List<String>ingredients_added;
	
	public void clear(){
		while(ingredients_added.size()>0) removeIngredient("0");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initialize(URL url, ResourceBundle rsrcbndl) {
		List<String> ingredient_database=new ArrayList<String>();
		Properties prop = new Properties();
		InputStream input = null;
	 	try {
	 		input = new FileInputStream("ingredient_database.properties");
	 		prop.load(input);
	 		for(int i=1;true;i++){
	 			if(prop.getProperty(i+"")==null) break;
	 			ingredient_database.add(prop.getProperty(i+""));
	 		}
	 	} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		
		ingredient_factory=FXCollections.observableArrayList(ingredient_database);
		
		search_ingredient=new AutoFillTextBox(ingredient_factory);
		search_ingredient.setScaleX(1.6);
		search_ingredient.setScaleY(1.6);
		search_ingredient.setLayoutX(42);
		search_ingredient.setLayoutY(40);
		search_ingredient.setId("search_ingredient");
		anchorpane.getChildren().addAll(search_ingredient);
		ingredient_list.setFocusTraversable(false);
		ingredients_added=new ArrayList<String>();
	}
	
	public void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	int starting_point=0;
	
	public void build_it(){
		if(!(ingredients_added.size()>0)) return;
		recipe=new Recipe_Maker(); 
		final ScrollPane scrollPaneDisplay=new ScrollPane();
		final Pane content=new Pane();
		scrollPaneDisplay.setStyle("-fx-background-color:white");
		content.setStyle("-fx-background-color:white");
		final String ingredient_array[]=new String[ingredients_added.size()];
		for(int i=0;i<ingredients_added.size();i++)	ingredient_array[i]=ingredients_added.get(i);
		
		try {
			@SuppressWarnings("rawtypes")
			final Map output_recipes=recipe.getRecipes(ingredient_array, false,40+40,starting_point);
			final int spacing=280;
			
			scrollPaneDisplay.setContent(content);
			display_content.setCenter(scrollPaneDisplay);  
			
			final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			 
			 scheduler.scheduleAtFixedRate(new Runnable(){
							public void run() {
								for(int i=0;i<output_recipes.size();i++){
									
									final Label lbl_recipe_name=new Label("\tName: ");
									lbl_recipe_name.setStyle("-fx-font-weight:bold;");
									lbl_recipe_name.setLayoutY(i*spacing+17);
									lbl_recipe_name.setLayoutX(8);
									
									final Label recipe_enum=new Label("Recipe "+(i+starting_point+1)+": ");
									recipe_enum.setStyle("-fx-font-weight:bold;-fx-font-size: 17px;-fx-text-fill: green;");
									recipe_enum.setLayoutY(i*spacing-5);
									recipe_enum.setLayoutX(10);
									
									@SuppressWarnings("rawtypes")
									final Label recipe_name=new Label((String) ((ArrayList)output_recipes.get(i)).get(0));
									
									recipe_name.setLayoutY(i*spacing+17);
									recipe_name.setLayoutX(75);
									
									@SuppressWarnings("rawtypes")
									final Hyperlink recipe_link = new Hyperlink((String) ((ArrayList)output_recipes.get(i)).get(1));
									final Label lbl_recipe_link=new Label("\tLink: ");
									lbl_recipe_link.setStyle("-fx-font-weight:bold;");
									lbl_recipe_link.setLayoutX(8);
									lbl_recipe_link.setLayoutY(i*spacing+35);
									recipe_link.setOnAction(new EventHandler<ActionEvent>() {
										public void handle(ActionEvent evt) {
											try {openWebpage(new URI(recipe_link.getText()));
										    } catch (URISyntaxException e) {e.printStackTrace();}
										}
									});
									
									recipe_link.setLayoutY(i*spacing+32);
									recipe_link.setLayoutX(60);
									recipe_link.setStyle("-fx-text-fill: blue;");
									
									@SuppressWarnings("rawtypes")
									final Hyperlink recipe_image_url = new Hyperlink((String) ((ArrayList)output_recipes.get(i)).get(2));
									recipe_image_url.setLayoutX(70);
									recipe_image_url.setLayoutY(i*spacing+50);
									recipe_image_url.setOnAction(new EventHandler<ActionEvent>() {
										public void handle(ActionEvent evt) {
											try {openWebpage(new URI(recipe_image_url.getText()));
										    } catch (URISyntaxException e) {e.printStackTrace();}
										}
									});
									recipe_image_url.setStyle("-fx-text-fill: blue;");
									
									final Label recipe_image=new Label("");
									   
									Image isimgnull;
									Node node=ImageViewBuilder.create().image((isimgnull=new Image(recipe_image_url.getText()))).build();
									if(isimgnull.getHeight()==0.0) recipe_image.setGraphic(new ImageView(new Image("/noimage.jpg")));
									else recipe_image.setGraphic(node);
									recipe_image.setScaleX(1);
									recipe_image.setScaleY(1);
									recipe_image.setLayoutX(35);
									recipe_image.setLayoutY(i*spacing+70);
									
									final Label lbl_recipe_image_url=new Label("\tImage: ");
									lbl_recipe_image_url.setStyle("-fx-font-weight:bold;");
									lbl_recipe_image_url.setLayoutX(8);
									lbl_recipe_image_url.setLayoutY(i*spacing+53);
									
									final Label lbl_recipe_needs=new Label("\tNeeds: ");
									lbl_recipe_needs.setStyle("-fx-font-weight:bold;");
									lbl_recipe_needs.setLayoutX(8);
									lbl_recipe_needs.setLayoutY(i*spacing+spacing-20);
									@SuppressWarnings("rawtypes")
									final Label recipe_needs=new Label(((String) ((ArrayList)output_recipes.get(i)).get(3)).replaceAll("\\[|\\]", ""));
									recipe_needs.setLayoutX(75);
									recipe_needs.setLayoutY(i*spacing+spacing-20);
									
									final Button goDnBtn=new Button(""+i);
									if(i==output_recipes.size()-1){
										goDnBtn.setText("Go Down");
										goDnBtn.setLayoutX(400);
										goDnBtn.setLayoutY(i*spacing+spacing+25);
										goDnBtn.setPrefSize(200, 30);
										goDnBtn.setOnAction(new EventHandler<ActionEvent>(){
											public void handle(ActionEvent event) {
												starting_point+=80;
												build_it();
											}
										});
									}
									
									final Button goUpBtn=new Button("Go Up");
									goUpBtn.setLayoutX(400);
									goUpBtn.setLayoutY(0);
									goUpBtn.setPrefSize(200, 30);
									goUpBtn.setOnAction(new EventHandler<ActionEvent>(){
										public void handle(ActionEvent event) {
											starting_point-=80;
											build_it();
										}
									});
									
									
									Platform.runLater(new Runnable(){
		                                public void run() {
											content.getChildren().add(lbl_recipe_name);
		                                	content.getChildren().add(recipe_name);
											content.getChildren().add(recipe_link);
											content.getChildren().add(lbl_recipe_link);
											content.getChildren().add(lbl_recipe_image_url);
											content.getChildren().add(recipe_image_url);
											content.getChildren().add(recipe_image);
											content.getChildren().add(recipe_needs);
											content.getChildren().add(lbl_recipe_needs);
											content.getChildren().add(recipe_enum);
											if(goDnBtn.getText().matches("Go Down")) content.getChildren().add(goDnBtn);
											if(starting_point!=0) content.getChildren().add(goUpBtn);
										}
		                            });
								}
								scheduler.shutdown();
								
							}},100,100,TimeUnit.MICROSECONDS);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void make_recipes_btn(){
		starting_point=0;
		build_it();
	}
	
	int lbl_y=-5;
	public void addIngredient(){
		if(!ingredient_factory.contains(search_ingredient.getText()) || ingredients_added.contains(search_ingredient.getText())) return;
		ingredients_added.add(search_ingredient.getText());
		
		final Label lbl=new Label(ingredients_added.indexOf(search_ingredient.getText())+1+": "+search_ingredient.getText());
		
		lbl.setLayoutY(lbl_y+=15);
		lbl.setLayoutX(5);
		final Label xlbl=new Label("X");
		xlbl.setId(ingredients_added.indexOf(search_ingredient.getText())+"");
		xlbl.setLayoutY(lbl_y);
		xlbl.setLayoutX(220);
		
		xlbl.setOnMouseEntered(new EventHandler<MouseEvent>() {
		      public void handle(MouseEvent e) {
		    	  xlbl.setStyle("-fx-font-weight: bold;");
		    	  lbl.setStyle("-fx-background-color: #E6E6E6;");
		      }
		    });
		xlbl.setOnMouseExited(new EventHandler<MouseEvent>() {
		      public void handle(MouseEvent e) {
		    	  xlbl.setStyle("-fx-font-weight: normal;");
		    	  lbl.setStyle("-fx-background-color: #F4F4F4;");
		      }
		    });
		xlbl.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e) {
				removeIngredient(xlbl.getId());
			}			
		});
		ingredient_list.getChildren().add(lbl);
		ingredient_list.getChildren().add(xlbl);
	}
	public void removeIngredient(String ingredient_id){
		ingredients_added.remove(Integer.parseInt(ingredient_id));
		ingredient_list.getChildren().clear();
		lbl_y=-5;
		for(int i=0;i<ingredients_added.size();i++){
			final Label lbl=new Label((i+1)+": "+ingredients_added.get(i));
			lbl.setLayoutY(lbl_y+=15);
			lbl.setLayoutX(5);
			final Label xlbl=new Label("X");
			xlbl.setId(i+"");
			xlbl.setLayoutY(lbl_y);
			xlbl.setLayoutX(220);
			xlbl.setOnMouseEntered(new EventHandler<MouseEvent>() {
			      public void handle(MouseEvent e) {
			    	  xlbl.setStyle("-fx-font-weight: bold;");
			    	  lbl.setStyle("-fx-background-color: #E6E6E6;");
			      }
			    });
			xlbl.setOnMouseExited(new EventHandler<MouseEvent>() {
			      public void handle(MouseEvent e) {
			    	  xlbl.setStyle("-fx-font-weight: normal;");
			    	  lbl.setStyle("-fx-background-color: #F4F4F4;");
			      }
			    });
			xlbl.setOnMouseClicked(new EventHandler<MouseEvent>(){
				public void handle(MouseEvent e) {
					removeIngredient(xlbl.getId());
				}			
			});
			ingredient_list.getChildren().add(lbl);
			ingredient_list.getChildren().add(xlbl);
		}
	}
}