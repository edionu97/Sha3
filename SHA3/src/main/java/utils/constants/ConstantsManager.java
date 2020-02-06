package utils.constants;


import utils.constants.model.Constants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConstantsManager {

    private static volatile ConstantsManager instance;
    private Map<String, String> keys = new HashMap<>();

    private ConstantsManager() {

        try {

            final JAXBContext context = JAXBContext.newInstance(Constants.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final ClassLoader loader = ConstantsManager.class.getClassLoader();

            final File f = new File(Objects
                    .requireNonNull(loader.getResource("constants.xml"))
                    .getFile()
                    .replaceAll("%20", " ")
            );

            _createKeyMap(
                    (Constants) unmarshaller.unmarshal(f)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates an instance of the object
     *
     * @return an instance of ConstantsManager class
     */
    public static ConstantsManager getInstance() {

        if (instance == null) {
            synchronized (ConstantsManager.class) {
                if (instance == null) {
                    instance = new ConstantsManager();
                }
            }
        }

        return instance;
    }

    /**
     * Searches in map for the property with name @param propertyName
     *
     * @param propertyName: the name of the property
     * @return a string representing the value of the property or null if the property does not exist
     */
    public String get(final String propertyName) {
        return keys.get(propertyName);
    }

    /**
     * Creates the map from the object fields
     *
     * @param constants: the constant object that represents xml file mapping to object
     * @throws Exception: if something is wrong
     */
    private void _createKeyMap(final Constants constants) throws Exception {

        for (Field field : constants.getClass().getDeclaredFields()) {

            final String fieldName = field.getName();

            final String result = (String) new PropertyDescriptor(
                    fieldName,
                    Constants.class
            ).getReadMethod().invoke(constants);

            keys.put(
                    fieldName, result
            );
        }
    }
}