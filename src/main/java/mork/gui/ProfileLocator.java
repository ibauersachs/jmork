package mork.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Tries to find the first address book of the current user on the local machine
 * and suggest the absolute path to the address book mork file.
 * 
 * @author mhaller
 */
class ProfileLocator
{

   private File getThunderbirdDir()
   {
      // 1. Try environment variable "APPDATA"/Thunderbird/
      String appData = System.getenv().get("APPDATA");
      if (appData != null && new File(appData).exists())
      {
         File appDataFolder = new File(appData);
         if (appDataFolder.isDirectory())
         {
            File thunderbird = new File(appDataFolder, "Thunderbird");
            if (thunderbird.exists() && thunderbird.isDirectory() )
            {
               return thunderbird;
            }
         }
      }
      
      // 2. Try "<user.home>/.mozilla-thunderbird
      File userhome = new File(System.getProperty("user.home"));
      File thunderbird = new File(userhome, ".mozilla-thunderbird");
      if (thunderbird.exists() && thunderbird.isDirectory())
      {
         return thunderbird;
      }
      
      return null;
   }

   public String locateFirstThunderbirdAddressbookPath()
   {
      File thunderbird = getThunderbirdDir();
      if (thunderbird == null)
      {
         return null;
      }
      File profiles = new File(thunderbird, "profiles.ini");
      if (!profiles.exists())
      {
         return null;
      }
      if (!profiles.isFile())
      {
         return null;
      }
      if (!profiles.canRead())
      {
         return null;
      }
      Properties props = new Properties();
      try
      {
         props.load(new FileInputStream(profiles));
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      if (!props.containsKey("Path"))
      {
         return null;
      }
      String path = (String) props.getProperty("Path");
      File firstProfileFolder = new File(thunderbird, path);
      if (!firstProfileFolder.exists())
      {
         return null;
      }
      if (!firstProfileFolder.isDirectory())
      {
         return null;
      }
      return firstProfileFolder.getAbsolutePath();
   }

}
