package io.should.proxy.lib.reflection;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;

import io.should.proxy.lib.APL;
import io.should.proxy.lib.log.LogWrapper;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils
{
    public static final String TAG = "ReflectionUtils";

    public static void connectToWifi(WifiManager wifiManager, Integer networkId) throws Exception
    {
        boolean internalConnectDone = false;

        try
        {
            Class[] knownParam = new Class[1];
            knownParam[0] = int.class;
            Method internalConnect = getMethod(WifiManager.class.getMethods(), "connect", knownParam);
            if (internalConnect != null)
            {
                Class<?>[] paramsTypes = internalConnect.getParameterTypes();
                if (paramsTypes.length == 2)
                    internalConnect.invoke(wifiManager, networkId, null);
                else if (paramsTypes.length == 1)
                    internalConnect.invoke(wifiManager, networkId);

                internalConnectDone = true;
            }
        }
        catch (Exception e)
        {
            APL.getEventReport().send(e);
        }

        if (!internalConnectDone)
        {
            // Use the STANDARD API as a fallback solution
            wifiManager.enableNetwork(networkId, true);
        }
    }

    public static void saveWifiConfiguration(WifiManager wifiManager, WifiConfiguration configuration) throws Exception
    {
        boolean internalSaveDone = false;

        /**
         *   4.4.2_r1
         *   4.4.1_r1
         *   4.4_r1
         *   4.3.1_r1
         *   4.3_r1
         *   4.2.2_r1
         *   4.2.1_r1.2
         *   4.2_r1 public void More ...save(WifiConfiguration config, ActionListener listener)
         *
         *   4.1.2_r1
         *   4.1.1_r1 public void More ...save(Channel c, WifiConfiguration config, ActionListener listener)
         *
         *   4.0.4_r2.1
         *   4.0.4_r1.2
         *   4.0.3_r1
         *   4.0.2_r1
         *   4.0.1_r1 public void More ...saveNetwork(WifiConfiguration config)
         *
         * */

        try
        {
            switch (Build.VERSION.SDK_INT)
            {
                case 14:
                case 15:
                    internalSaveDone =  save_4_0(wifiManager, configuration);
                    break;

                case 16:
                    internalSaveDone =  save_4_1(wifiManager, configuration);
                    break;

                case 17:
                case 18:
                case 19:
                    internalSaveDone = save_4_2(wifiManager, configuration);
                    break;

                default:
                    internalSaveDone = saveNoVersion(wifiManager, configuration);
                    break;
            }
        }
        catch (Exception e)
        {
            APL.getEventReport().send(new Exception("Exception during saveWifiConfiguration", e));
        }

        if (!internalSaveDone)
        {
            // Use the STANDARD API as a fallback solution
            wifiManager.updateNetwork(configuration);
        }
    }

    private static boolean save_4_0(WifiManager wifiManager, WifiConfiguration configuration) throws Exception
    {
        boolean internalSaveDone = false;

        Method internalSaveNetwork = getMethod(WifiManager.class.getMethods(), "saveNetwork");
        if (internalSaveNetwork != null)
        {
            try
            {
                internalSaveNetwork.invoke(wifiManager, configuration);
                internalSaveDone = true;
            }
            catch (Exception e)
            {
                throw new Exception("Exception during call of WifiManager.saveNetwork method (4.0) : " + e, e);
            }
        }

        return internalSaveDone;
    }

    private static boolean save_4_1(WifiManager wifiManager, WifiConfiguration configuration) throws Exception
    {
        boolean internalSaveDone = false;

        Method internalSave = getMethod(WifiManager.class.getMethods(), "save");
        if (internalSave != null)
        {
            try
            {
                internalSave.invoke(wifiManager, configuration, null);
                internalSaveDone = true;
            }
            catch (Exception e)
            {
                throw new Exception("Exception during call of WifiManager.save method (4.1) : " + e,e);
            }
        }

        return internalSaveDone;
    }

    private static boolean save_4_2(WifiManager wifiManager, WifiConfiguration configuration) throws Exception
    {
        boolean internalSaveDone = false;

        Method internalSave = getMethod(WifiManager.class.getMethods(), "save");
        if (internalSave != null)
        {
            try
            {
                /**
                 * TODO: needs pass an instance of the interface WifiManager.ActionListener, in order to receive the status of the call
                 */
                internalSave.invoke(wifiManager, configuration, null);
                internalSaveDone = true;
            }
            catch (Exception e)
            {
                throw new Exception("Exception during call of WifiManager.save method (4.4) : " + e, e);
            }
        }

        return internalSaveDone;
    }


    private static boolean saveNoVersion(WifiManager wifiManager, WifiConfiguration configuration) throws Exception
    {
        boolean internalSaveDone = false;

        Method internalSaveNetwork = getMethod(WifiManager.class.getMethods(), "saveNetwork");
        Method internalSave = getMethod(WifiManager.class.getMethods(), "save");

        if (internalSave != null)
        {
            Class<?>[] paramsTypes = internalSave.getParameterTypes();
            if (paramsTypes.length == 2)
            {
                /**
                 * TODO: needs pass an instance of the interface WifiManager.ActionListener, in order to receive the status of the call
                 */
                internalSave.invoke(wifiManager, configuration, null);
                internalSaveDone = true;
            }
            else if (paramsTypes.length == 1)
            {
                internalSave.invoke(wifiManager, configuration);
                internalSaveDone = true;
            }
            else
            {
                APL.getEventReport().send(new Exception("Not handled WifiManager.save method. Found params: " + paramsTypes.length));
            }
        }

        return internalSaveDone;
    }

    public static Method getMethod(Method[] methods, String methodName) throws Exception
    {
        Method m = null;

        for (Method lm : methods)
        {
            String currentMethodName = lm.getName();
            if (currentMethodName.equals(methodName))
            {
                m = lm;
                break;
            }
        }

        if (m == null)
            throw new Exception(methodName + " method not found!");

        return m;
    }

    public static Method getMethod(Method[] methods, String methodName, Class[] knownParameters) throws Exception
    {
        Method m = null;

        for (Method lm : methods)
        {
            String currentMethodName = lm.getName();
            if (currentMethodName.equals(methodName))
            {
                Boolean found = false;

                for (Class knowParam : knownParameters)
                {
                    found = false;
                    for (Class param : lm.getParameterTypes())
                    {
                        if (param.getName().equals(knowParam.getName()))
                        {
                            found = true;
                            break;
                        }
                    }

                    if (found == false)
                        break;
                }

                if (found)
                {
                    m = lm;
                    break;
                }
            }
        }

        if (m == null)
            throw new Exception(methodName + " method not found!");

        return m;
    }

    public static List<Method> getMethods(Method[] methods, String methodName) throws Exception
    {
        ArrayList<Method> ml = new ArrayList<Method>();

        for (Method lm : methods)
        {
            String currentMethodName = lm.getName();
            if (currentMethodName.equals(methodName))
            {
                ml.add(lm);
            }
        }

        if (ml.size() == 0)
            throw new Exception(methodName + " method not found!");

        return ml;
    }

    public static Field getField(Field[] fields, String fieldName) throws Exception
    {
        Field f = null;

        for (Field lf : fields)
        {
            String currentFieldName = lf.getName();
            if (currentFieldName.equals(fieldName))
            {
                f = lf;
                break;
            }
        }

        if (f == null)
            throw new Exception(fieldName + " field not found!");

        return f;
    }

    public static Class getDeclaredClass(Class[] classes, String className) throws Exception
    {
        Class c = null;

        for (Class lc : classes)
        {
            String currentFieldName = lc.getName();
            if (currentFieldName.equals(className))
            {
                c = lc;
                break;
            }
        }

        if (c == null)
            throw new Exception(className + " class not found!");

        return c;
    }

    static void describeClassOrInterface(Class className, String name)
    {
        displayModifiers(className.getModifiers());
        displayFields(className.getDeclaredFields());
        displayMethods(className.getDeclaredMethods());

        if (className.isInterface())
        {
            LogWrapper.d(TAG, "Interface: " + name);
        }
        else
        {
            LogWrapper.d(TAG, "Class: " + name);
            displayInterfaces(className.getInterfaces());
            displayConstructors(className.getDeclaredConstructors());
        }
    }

    static void displayModifiers(int m)
    {
        LogWrapper.d(TAG, "Modifiers: " + Modifier.toString(m));
    }

    static void displayInterfaces(Class[] interfaces)
    {
        if (interfaces.length > 0)
        {
            LogWrapper.d(TAG, "Interfaces: ");
            for (int i = 0; i < interfaces.length; ++i)
                LogWrapper.d("", interfaces[i].getName());
        }
    }

    static void displayFields(Field[] fields)
    {
        if (fields.length > 0)
        {
            LogWrapper.d(TAG, "Fields: ");
            for (int i = 0; i < fields.length; ++i)
                LogWrapper.d(TAG, fields[i].toString());
        }
    }

    static void displayConstructors(Constructor[] constructors)
    {
        if (constructors.length > 0)
        {
            LogWrapper.d(TAG, "Constructors: ");
            for (int i = 0; i < constructors.length; ++i)
                LogWrapper.d(TAG, constructors[i].toString());
        }
    }

    static void displayMethods(Method[] methods)
    {
        if (methods.length > 0)
        {
            LogWrapper.d(TAG, "Methods: ");
            for (int i = 0; i < methods.length; ++i)
                LogWrapper.d(TAG, methods[i].toString());
        }
    }

    public static Field[] getAllFields(Class klass)
    {
        List<Field> fields = new ArrayList<Field>();
        fields.addAll(Arrays.asList(klass.getDeclaredFields()));

        if (klass.getSuperclass() != null)
        {
            fields.addAll(Arrays.asList(getAllFields(klass.getSuperclass())));
        }

        return new Field[]{};
    }
}
