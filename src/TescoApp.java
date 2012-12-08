import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.*;

public class TescoApp extends MIDlet implements CommandListener, ItemCommandListener{
  private List list;
  private static final Command nextCommand = new Command("Next", Command.SCREEN, 1);
  private static final Command exitCommand = new Command("Exit", Command.EXIT, 1);
  private static final Command backCommand = new Command("Back", Command.BACK, 1);
  private static final Command deleteCommand = new Command("Delete item", Command.SCREEN, 1);
  private static final Command select = new Command("add", Command.OK, 1);
  private StringItem item = new StringItem("", "Submit", Item.BUTTON);
  private static final Command CMD_PRESS = new Command("Press", Command.ITEM, 1);
  private TextField textF = new TextField("Add item to list", "", 30, TextField.ANY); 
  private Form mainForm; 
  private RecordStore rs;
  private RecordStore shoppingCart;
  private int num = 0;
  private List newList;
  private ItemStateListener listener;

  public TescoApp() {
    mainForm = new Form("");
    list = new List("UIitems", Choice.IMPLICIT);
    list.append("Add Item", null);
    list.append("Choose from existing", null);
    list.append("See shopping cart", null);
	list.append("Clear shopping cart", null);
	list.addCommand(nextCommand);
    list.addCommand(exitCommand);
    list.setCommandListener(this);
	try{
		rs = RecordStore.openRecordStore("Tesco database", true );
		shoppingCart = RecordStore.openRecordStore("Shopping cart", true );
	}
	catch (Exception error){
		Alert alert = new Alert("Exception", error.toString(), null, 
		AlertType.WARNING); 
		alert.setTimeout(Alert.FOREVER); 
		Display.getDisplay(this).setCurrent(alert);
	}
  }
  
  public void startApp() {
    Display.getDisplay(this).setCurrent(list);
  }
  
   public void commandAction(Command c, Displayable displayable){
		String label = c.getLabel();
		if (c == nextCommand || c == List.SELECT_COMMAND) {
			int index = list.getSelectedIndex();
			if(list.getString(index).equals("Add Item")){
				mainForm.append(textF);
				mainForm.addCommand(backCommand);
				mainForm.addCommand(exitCommand);
				mainForm.append(item);
				item.setDefaultCommand(CMD_PRESS);
				item.setItemCommandListener(this);
				mainForm.setCommandListener(this);
				Display.getDisplay(this).setCurrent(mainForm);
			}
			if(list.getString(index).equals("Choose from existing")){
				try{
				    newList = new List("UIitems", Choice.IMPLICIT);
					RecordEnumeration record = rs.enumerateRecords(null, null, false);
					if(record != null){
					while(record.hasNextElement()){				
						newList.append(new String(record.nextRecord()), null);
					}
					}
				}
				catch (Exception error){
					Alert alert = new Alert("Exception", error.toString(), null, 
					AlertType.WARNING); 
					alert.setTimeout(Alert.FOREVER); 
					Display.getDisplay(this).setCurrent(alert);
				}
				newList.addCommand(backCommand);
				newList.addCommand(select);
				newList.setCommandListener(this);
				Display.getDisplay(this).setCurrent(newList);
			}
			if(list.getString(index).equals("See shopping cart")){
				try{
				    newList = new List("UIitems", Choice.IMPLICIT);
					RecordEnumeration shop = shoppingCart.enumerateRecords(null, null, false);
					while(shop.hasNextElement()){				
						newList.append(new String(shop.nextRecord()), null);
					}
				}
				catch (Exception error){
					Alert alert = new Alert("Exception", error.toString(), null, 
					AlertType.WARNING); 
					alert.setTimeout(Alert.FOREVER); 
					Display.getDisplay(this).setCurrent(alert);
				}
				newList.addCommand(backCommand);
				newList.addCommand(deleteCommand);
				newList.setCommandListener(this);
				Display.getDisplay(this).setCurrent(newList);
			}
			if(list.getString(index).equals("Clear shopping cart")){
				try{
				    Alert alert = new Alert("Cart Cleared", "Cart Cleared", null, AlertType.INFO);
					alert.setTimeout(1000);
					Display.getDisplay(this).setCurrent(alert, list);
					shoppingCart.closeRecordStore();
					RecordStore.deleteRecordStore("Shopping cart");
					shoppingCart = RecordStore.openRecordStore("Shopping cart", true );
				}
				catch (Exception error){
					Alert alert = new Alert("Exception", error.toString(), null, 
					AlertType.WARNING); 
					alert.setTimeout(Alert.FOREVER); 
					Display.getDisplay(this).setCurrent(alert);
				}
			}
		}
		
		else if (c == exitCommand){
			notifyDestroyed();
		}
		else if(c == backCommand){
			textF.setString(null);
			newList = null;
			mainForm.deleteAll();
			Display.getDisplay(this).setCurrent(list);
		}
		else if(c == select){
			try{
			    Alert alert = new Alert("Added to your cart", "Item Added to your cart", null, AlertType.INFO);
				alert.setTimeout(1000);
				Display.getDisplay(this).setCurrent(alert, newList);
				int selected = newList.getSelectedIndex();
				String newAdd = newList.getString(selected);
				byte bytes[] = newAdd.getBytes();
				shoppingCart.addRecord(bytes,0,bytes.length);
			}
			catch (Exception error){
				Alert alert = new Alert("Exception", error.toString(), null, 
				AlertType.WARNING); 
				alert.setTimeout(Alert.FOREVER); 
				Display.getDisplay(this).setCurrent(alert);
			}
			Display.getDisplay(this).setCurrent(newList);
		}
		
		else if(c == deleteCommand){
			try{
				int selected = newList.getSelectedIndex();
				newList.delete(selected);
				shoppingCart.deleteRecord(selected+1);
			}
			catch (Exception error){
				Alert alert = new Alert("Exception", error.toString(), null, 
				AlertType.WARNING); 
				alert.setTimeout(Alert.FOREVER); 
				Display.getDisplay(this).setCurrent(alert);
			}
			Display.getDisplay(this).setCurrent(newList);
		}
	} 

	public void commandAction(Command c, Item item) {
        if(c == CMD_PRESS){
			 try{
				String t = textF.getString();
				String record = t;
				byte bytes[] = record.getBytes();
				rs.addRecord(bytes,0,bytes.length);
				mainForm.deleteAll();
				textF.setString(null);
				Display.getDisplay(this).setCurrent(list);
			}
			catch (Exception error){
				Alert alert = new Alert("Exception", error.toString(), null, 
				AlertType.WARNING); 
				alert.setTimeout(Alert.FOREVER); 
				Display.getDisplay(this).setCurrent(alert);
			}
		}
    }	
  
  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {
   try{
		rs.closeRecordStore();
		shoppingCart.closeRecordStore();
	}
	catch (Exception error){
		Alert alert = new Alert("Exception", error.toString(), null, 
		AlertType.WARNING); 
		alert.setTimeout(Alert.FOREVER); 
		Display.getDisplay(this).setCurrent(alert);
	}
  }
}


