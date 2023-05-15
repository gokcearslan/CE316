package com.example.ce316team5project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.sql.Statement;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class Controller implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField filePathBox;

    @FXML
    private TextArea outputArea;

    @FXML
    private TextField className;
    @FXML
    private TextField configName;
    @FXML
    private ChoiceBox<String> configurationBox = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> languageBox = new ChoiceBox<>();

    @FXML
    private TextField expectedOutputPathBox;

    @FXML
    private TextArea expectedOutputTxt;

    @FXML
    private TextArea inputTxt;

    @FXML
    private TableView<Student> ResultTW = new TableView<>();

    @FXML
    private TableColumn<Student, String> StudentResulTC = new TableColumn<>();

    @FXML
    private ChoiceBox<String> projectNameCB = new ChoiceBox<>();

    @FXML
    private TableColumn<Student, String> studentIDTC = new TableColumn<>();

    @FXML
    private TextField projectname = new TextField();
    @FXML
    private Button deneme;
    @FXML
    private TextField Studentid;
    @FXML
    private TabPane TabPane = new TabPane();

    @FXML
    private Tab EditTab = new Tab();

    @FXML
    private Tab ListTab = new Tab();

    @FXML
    private TextField configNameTxt = new TextField();

    @FXML
    private TableColumn<Configuration, String> editConfigLang = new TableColumn<>();

    @FXML
    private TableColumn<Configuration, String> editConfigName = new TableColumn<>();

    @FXML
    private TableColumn<Configuration, String> editGivenInput = new TableColumn<>();

    @FXML
    private TableView<Configuration> editPageTW = new TableView<>();

    @FXML
    private ChoiceBox<String> columnNameTextField = new ChoiceBox<>();

    @FXML
    private TextField columnNameTextField1;

    @FXML
    private TableColumn<Configuration, String> editAttributeName = new TableColumn<>();

    @FXML
    private TableColumn<Configuration, String> editAttributeValue = new TableColumn<>();


    @FXML
    private TableView<Configuration> editTab2TW = new TableView<>();
    Configuration configuration = null;

    ArrayList<Configuration> configurations = new ArrayList<>();

    public void compileAndExecuteWithParameter(List<File> files) throws InterruptedException, IOException {

        if (this.configuration != null) {
            String configurationName = this.configuration.getInput();
        } else {
            System.out.println("Configuration is not initialized yet.");
        }

        for (File file : files) {
            String filePath = file.getAbsolutePath();

            // getConfiguration().setFilePath(filePath);
            // System.out.println("filepath: " + getConfiguration().getFilePath());

            ProcessBuilder compileProcessBuilder = null;
//String inputListFromUser = getConfiguration().getInput();
            String inputListFromUser = this.configuration.getInput();
            System.out.println("input: " + inputListFromUser);

            String[] numbers = inputListFromUser.split(" ");
            List<String> executionArgs = new ArrayList<>(Arrays.asList(numbers));

            if (getConfiguration().getLanguage().equals("Java")) {
                System.out.println("language name: " + getConfiguration().getLanguage());
                compileProcessBuilder = new ProcessBuilder("javac", filePath);
                System.out.println("compileProcessBuilder " + "javac" + "" + filePath);
            } else if (getConfiguration().getLanguage().equals("C")) {
                compileProcessBuilder = new ProcessBuilder("gcc", "-o", "a.out", filePath);
            } else if (getConfiguration().getLanguage().equals("Python")) {
                // Python
                compileProcessBuilder = new ProcessBuilder("python", filePath);

            } else {
                System.out.println("Language does not exists.");
            }


            // For Java and C
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();

            // Check if the compilation process was successful
            if (compileProcess.exitValue() != 0) {
                printProcessErrors(compileProcess);
                throw new RuntimeException("Compilation failed. Check the error stream for more details.");
            }

            // Execute the compiled file
            ProcessBuilder executeProcessBuilder = new ProcessBuilder();
            Process executeProcess;

            if (getConfiguration().getLanguage().equals("Java")) {


                String className = extractedClassName;
                System.out.println("class name: " + extractedClassName);

                executeProcessBuilder = new ProcessBuilder("java", className);
                System.out.println("executeProcessBuilder: " + " java " + className);


            } else if (getConfiguration().getLanguage().equals("C")) {

                executeProcessBuilder = new ProcessBuilder("./a.out");

            } else if (getConfiguration().getLanguage().equals("Python")) {
                executeProcessBuilder = new ProcessBuilder("python", filePath);

            }


            executeProcessBuilder.directory(new File(filePath).getParentFile());
            executeProcessBuilder.command().addAll(executionArgs);
            executeProcess = executeProcessBuilder.start();


   /* File file = new File(filePath);
    String castPath = file.getParent();
    executeProcessBuilder.directory(new File(castPath));
    executeProcessBuilder.command().addAll(executionArgs); // Add this line
    Process executeProcess = executeProcessBuilder.start();

    */

            // Check if the execution process was successful
            if (executeProcess.waitFor() != 0) {
                printProcessErrors(executeProcess);
                throw new RuntimeException("Execution failed. Check the error stream for more details.");
            }
            // Output the result of the execution

            printProcessOutput(executeProcess);


            //Buraya eklenir

            //String studentId = studentIdTextField.getText(); // Replace studentIdTextField with the actual TextField object for the student ID



            String filePathid = file.getAbsolutePath();
            int lastSeparatorIndex = filePathid.lastIndexOf(File.separator);
            // Assuming the student ID is always the folder name before the last separator
            int secondLastSeparatorIndex = filePathid.lastIndexOf(File.separator, lastSeparatorIndex - 1);
            String studentId = filePathid.substring(secondLastSeparatorIndex + 1, lastSeparatorIndex);
            System.out.println("student id: " + studentId);


            String projectName = projectname.getText();
            String studentSourceCodePath = filePathid; // Assuming filePathBox already contains the student's source code file path
            String studentoutput = outputArea.getText();
            String expectedOutput2 = expectedOutputTxt.getText();
            String configname = configurationBox.getValue();
            saveProjectDetails(projectName, studentSourceCodePath);
            if (studentoutput == null) {
                studentoutput = "";
            }

            //save to project table

            try {
                // Load SQLite JDBC driver
                Class.forName("org.sqlite.JDBC");

                // Connect to the database
                connection = DriverManager.getConnection("jdbc:sqlite:database.db");

                // Check if the record already exists
                String query = "SELECT COUNT(*) FROM projects WHERE student_id = ? AND project_name = ? AND student_scr = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, studentId);
                statement.setString(2, projectName);
                statement.setString(3, studentSourceCodePath);

                ResultSet resultSet = statement.executeQuery();
                int count = resultSet.getInt(1);
                resultSet.close();
                statement.close();

                if (count == 0) {
                    // Insert the new record
                    String insertProjectSQL = "INSERT INTO projects(project_name, student_scr, student_output, expected_output, configuration_name, student_id) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmtProject = connection.prepareStatement(insertProjectSQL);
                    pstmtProject.setString(1, projectName);
                    pstmtProject.setString(2, studentSourceCodePath);
                    pstmtProject.setString(3, studentoutput);
                    pstmtProject.setString(4, expectedOutput2);
                    pstmtProject.setString(5, configname);
                    pstmtProject.setString(6, studentId);

                    pstmtProject.executeUpdate();
                    pstmtProject.close();

                    System.out.println("Record inserted.");
                } else {
                    System.out.println("The combination of student_id, project_name, and student_scr already exists.");
                }

                connection.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //save to student table

            // Replace projectNameTextField with the actual TextField object for the project name
            String studentOutput = outputArea.getText(); // Replace with the actual file path
            String studentResult;
            String expectedOutput = expectedOutputTxt.getText();

            if (studentOutput == null) {
                studentResult = "NULL";
            } else if (studentOutput.equals(expectedOutput)) {
                studentResult = "Success";
            } else {
                studentResult = "Failure";
            }


            try {
                // Load SQLite JDBC driver
                Class.forName("org.sqlite.JDBC");

                // Connect to the database
                connection = DriverManager.getConnection("jdbc:sqlite:database.db");

                // Check if the record already exists
                String query = "SELECT COUNT(*) FROM " + "student" + " WHERE student_id = ? AND project_name = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, studentId);
                statement.setString(2, projectName);

                ResultSet resultSet = statement.executeQuery();
                int count = resultSet.getInt(1);
                resultSet.close();
                statement.close();

                if (count == 0) {
                    // Insert the new record
                    String insertQuery = "INSERT INTO " + "student" + " (student_id, student_result,project_name) VALUES (?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setString(1, studentId);
                    insertStatement.setString(2, studentResult);
                    insertStatement.setString(3, projectName);

                    insertStatement.executeUpdate();
                    insertStatement.close();

                    System.out.println("Record inserted.");
                } else {
                    System.out.println("The combination of student_id and project_name already exists.");
                }

                connection.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void loadConfigurationNames() {
        // Check if the connection is null
        if (this.connection == null) {
            try {
                // Establish a connection to your database
                this.connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            } catch (SQLException e) {
                System.out.println("Failed to connect to the database");
                System.out.println(e.getMessage());
                return;
            }
        }

        String sql = "SELECT configuration_name FROM configuration";

        try {
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Clear the ChoiceBox
            configurationBox.getItems().clear();

            ArrayList<String> configNames = new ArrayList<>();
            // loop through the result set
            while (rs.next()) {
                String configurationName = rs.getString("configuration_name");
                configNames.add(configurationName);
            }
            configurationBox.getItems().addAll(configNames);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Configuration getConfiguration() {

        String config = configurationBox.getValue();

        Configuration configuration = null;
        String sql = "SELECT configuration_name, configuration_language, given_input FROM configuration WHERE configuration_name = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            // Set the value
            pstmt.setString(1, config);
            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                String configurationName = rs.getString("configuration_name");
                String configurationLanguage = rs.getString("configuration_language");
                String givenInput = rs.getString("given_input");
                this.configuration = new Configuration(configurationLanguage, configurationName, givenInput);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return this.configuration;
    }

    private List<String> getArgumentsList() {
        List<String> argsList = new ArrayList<>();
        String[] args = inputTxt.getText().split(" ");
        for (String arg : args) {
            if (!arg.isEmpty()) {
                argsList.add(arg);
            }
        }
        return argsList;
    }

    public void setCommands(String language, String className, String configName) {


        if (language.equals("Java")) {
            configuration = new Configuration(language, "javac", className, configName);
            configuration.setLanguage(language);
            configuration.setClassName(className);
            configuration.setConfigName(configName);

        } else if (language.equals("C")) {
            configuration = new Configuration(language, "gcc", null, configName);
            configuration.setLanguage(language);
            configuration.setConfigName(configName);

        } else if (language.equals("Python")) {
            configuration = new Configuration(language, "python", null, configName);
            configuration.setLanguage(language);
            configuration.setConfigName(configName);
        }
        configurations.add(configuration);

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String insertSQL = "INSERT INTO configuration(configuration_name, configuration_language, given_input) VALUES (?, ?, ?)";
        try {
            // Assuming connection is a valid Connection object
            PreparedStatement pstmt = connection.prepareStatement(insertSQL);
            pstmt.setString(1, configName);
            pstmt.setString(2, language);


// Assume argsList is filled with elements
            String result = String.join(" ", getArgumentsList());
            pstmt.setString(3, result);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<File> chooseFile() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        // Set the title for the dialog
        directoryChooser.setTitle("Select Project Directory");

        // Show the open directory dialog and store the selected directory in a variable
        File selectedParentDirectory = directoryChooser.showDialog(null);

        List<File> selectedFiles = unzipAll(selectedParentDirectory);

        if (selectedParentDirectory != null) {
            File[] directories = selectedParentDirectory.listFiles(File::isDirectory);
            if (directories != null) {
                for (File dir : directories) {
                    try {
                        // Walk the directory and find all .java, .c, and .py files
                        Files.walk(dir.toPath())
                                .filter(path -> path.toString().endsWith(".java") || path.toString().endsWith(".c") || path.toString().endsWith(".py"))
                                .forEach(path -> selectedFiles.add(path.toFile()));
                    } catch (IOException e) {
                        System.err.println("Error walking directory: " + e.getMessage());
                    }
                }
            }

            if (!selectedFiles.isEmpty()) {
                for (File selectedFile : selectedFiles) {
                    String selectedLanguage = getConfiguration().getLanguage();

                    if (selectedFile.getName().endsWith(".zip")) {
                        try {
                            selectedFile = unzipFile(selectedFile);
                        } catch (IOException e) {
                            System.err.println("Error unzipping file: " + e.getMessage());
                        }
                    }

                    filePathBox.setText(selectedFile.getAbsolutePath());

                    if (getConfiguration().getLanguage().equalsIgnoreCase("Java")) {
                        // Extract the class name from the file content
                        extractedClassName = extractClassName(selectedFile);
                    }
                }
              /*  // Handle the extraction of studentId here outside the loop.
                String filePathid = filePathBox.getText();
                int lastSeparatorIndex = filePathid.lastIndexOf('\\');

                // Assuming the student ID is always the folder name before the last separator
                int secondLastSeparatorIndex = filePathid.lastIndexOf('\\', lastSeparatorIndex - 1);
                String studentId = filePathid.substring(secondLastSeparatorIndex + 1, lastSeparatorIndex);
                extractedStudentID=studentId;

                System.out.println(studentId);
                Studentid.setText(studentId);

               */

            }
        }
        return selectedFiles;
    }


}

    public void createTable() {

        if (!isSetupDone) {
            try {
                // Create a connection
                connection = DriverManager.getConnection("jdbc:sqlite:database.db");
                // Create a Statement
                Statement statement = connection.createStatement();
                // Create a new table
                String configSql = "CREATE TABLE IF NOT EXISTS configuration(" +
                        "configuration_language TEXT NOT NULL," +
                        "configuration_name TEXT PRIMARY KEY," +
                        "given_input TEXT)";
                statement.execute(configSql);
                System.out.println("Table config created successfully");

                String createTableSQL = "CREATE TABLE IF NOT EXISTS projects (" +
                        "project_name TEXT(50)  NOT NULL," +
                        "student_scr TEXT(100) NOT NULL ," +
                        "student_output TEXT(100) NOT NULL," +
                        "expected_output TEXT(100) NOT NULL," +
                        "configuration_name TEXT," +
                        "student_id VARCHAR(50)  NOT NULL," +
                        "FOREIGN KEY (configuration_name) REFERENCES configuration(configuration_name)," +
                        "FOREIGN KEY (project_name, student_id) REFERENCES student(project_name, student_id)" + // Added foreign key constraint
                        ")";
                statement.execute(createTableSQL);
                System.out.println("Table createTableSQL created successfully");

                String studentTable = "CREATE TABLE IF NOT EXISTS student (" +
                        "student_id VARCHAR(50)  NOT NULL," +
                        "student_result BOOLEAN NOT NULL," +
                        "project_name TEXT(50) NOT NULL," +
                        "PRIMARY KEY (student_id, project_name)" + // Added composite primary key
                        ")";
                statement.execute(studentTable);
                System.out.println("Table studentTable created successfully");
                isSetupDone = true;


                // Close the statement
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createTable();
        languageBox.getItems().addAll("Java", "C", "Python");
        loadConfigurationNames();

        columnNameTextField.getItems().addAll("configuration_name", "given_input");
        loadConfigurationNames();
        createTable();
        ListConfig();
        ListStudentResults();


        // projectNameCB


        try {

            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Establish a connection to the database
            String url = "jdbc:sqlite:database.db";
            Connection connection = DriverManager.getConnection(url);

            String query = "SELECT DISTINCT project_name FROM student";
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            ObservableList<String> projectNames = FXCollections.observableArrayList();


            studentIDTC.setCellValueFactory(new PropertyValueFactory<>("student_id"));

            StudentResulTC.setCellValueFactory(new PropertyValueFactory<>("student_result"));

            ObservableList<Student> students = FXCollections.observableArrayList();
            ResultTW.setItems(students);

            while (resultSet.next()) {
                String projectName = resultSet.getString("project_name");
                projectNames.add(projectName);
            }

            resultSet.close();
            statement.close();

            projectNameCB.setItems(projectNames);

            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }



    }
    public void saveProjectDetails(String projectName, String studentSourceCodePath) {
        // Insert the projectName, studentId, and studentSourceCodePath into the projects table
        String insertProjectSQL = "INSERT INTO projects(project_name, student_scr) VALUES (?, ?)";
        try {
            PreparedStatement pstmtProject = connection.prepareStatement(insertProjectSQL);
            pstmtProject.setString(1, projectName);
            pstmtProject.setString(2, studentSourceCodePath);
            //pstmtProject.setString(3, studentId);
            pstmtProject.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private String extractClassName(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Pattern pattern = Pattern.compile("class\\s+(\\w+)");
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }
    public void ListStudentResults() {

        try {

            Class.forName("org.sqlite.JDBC");


            String url = "jdbc:sqlite:database.db";
            Connection connection = DriverManager.getConnection(url);


            String projectName =projectNameCB.getValue(); // get the project name from the choice box

            String query = "SELECT student_id, student_result FROM student WHERE project_name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, projectName);

            ResultSet resultSet = statement.executeQuery();

            ObservableList<Student> data = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String studentId = resultSet.getString("student_id");
                String studentResult = resultSet.getString("student_result");

                data.add(new Student(studentId, studentResult));
            }

            resultSet.close();
            statement.close();

            ResultTW.setItems(data);

            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void goToEditConfig() {

        String url = "jdbc:sqlite:database.db";

        if (configNameTxt.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a configuration name to edit.");
            alert.showAndWait();
            return;
        }

        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection(url);
            ObservableList<Configuration> data3 = FXCollections.observableArrayList();
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM configuration";
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int numColumns = metaData.getColumnCount();

            ArrayList<String> allColumnNames = new ArrayList<>();

            ObservableList<Configuration> list2 = FXCollections.observableArrayList(new Configuration("", ""));

            editAttributeName.setCellValueFactory(new PropertyValueFactory<Configuration, String>("X"));
            editAttributeValue.setCellValueFactory(new PropertyValueFactory<Configuration, String>("Y"));
            editTab2TW.setItems(list2);


            List<String> columnValues = new ArrayList<>();


            String configName = configNameTxt.getText();
            String sql2 = "SELECT * FROM configuration WHERE configuration_name = '" + configName + "'";


            ResultSet rs2 = stmt.executeQuery(sql2);

            while (rs2.next()) {

                for (int i = 1; i <= numColumns; i++) {


                    String columnName = metaData.getColumnName(i);
                    allColumnNames.add(columnName);

                    String columnValue = rs2.getString(i);
                    columnValues.add(columnValue);


                    Configuration temp2 = new Configuration(columnName, columnValue);

                    editTab2TW.getItems().add(temp2);


                }
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        TabPane.getSelectionModel().select(EditTab);

    }

    public void editMethod(ActionEvent b) throws IOException {


        String url = "jdbc:sqlite:database.db";
        if (columnNameTextField.getValue().isEmpty() || columnNameTextField1.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both a attribute name and a new attribute value name.");
            alert.showAndWait();
            return;
        }

        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(url);
            String columnName = columnNameTextField.getValue();
            String configName = columnNameTextField1.getText();

            String updateSQL = "UPDATE configuration SET " + columnName + " = ? WHERE configuration_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);

            preparedStatement.setString(1, configName);
            preparedStatement.setString(2, configNameTxt.getText());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Database update successful

                // Refresh the TableView with updated data
                ListConfig();


                columnNameTextField1.clear();

                root = FXMLLoader.load(getClass().getResource("WelcomePage.fxml"));
                stage = (Stage) ((Node) b.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                // No rows affected, handle the update failure
                System.out.println("Update failed. No rows affected.");
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void ListConfig() {
        String url6 = "jdbc:sqlite:database.db";
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(url6);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM configuration");

            editConfigName.setCellValueFactory(new PropertyValueFactory<Configuration, String>("configName"));
            editConfigLang.setCellValueFactory(new PropertyValueFactory<Configuration, String>("language"));
            editGivenInput.setCellValueFactory(new PropertyValueFactory<Configuration, String>("input"));


            List<Configuration> data2 = new ArrayList<>();

            while (rs.next()) {


                String value1 = rs.getString("configuration_name");
                String value2 = rs.getString("configuration_language");
                String value3 = rs.getString("given_input");


                data2.add(new Configuration(value1, value2, value3));

            }
            ObservableList<Configuration> ListPageData = FXCollections.observableArrayList(data2);
            editPageTW.setItems(ListPageData);


            conn.close();
        } catch (SQLException | ClassNotFoundException a) {
            a.printStackTrace();
        }
    }
    @FXML
    public void switchToConfig(ActionEvent e) throws IOException {

        root = FXMLLoader.load(getClass().getResource("Configuration.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(true);
    }

    @FXML
    public void switchToNewProject(ActionEvent e) throws IOException {

        root = FXMLLoader.load(getClass().getResource("NewProject.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(true);
    }

    public void switchToEditConfig(ActionEvent a) throws IOException {
        root = FXMLLoader.load(getClass().getResource("editPage.fxml"));
        stage = (Stage) ((Node) a.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(true);
    }
    public void deleteMethod(ActionEvent b) throws IOException {
        String url = "jdbc:sqlite:database.db";

        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(url);

            String deleteSQL = "DELETE FROM configuration WHERE configuration_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
            preparedStatement.setString(1, configNameTxt.getText());

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Row deleted successfully.");
            } else {
                System.out.println("No rows deleted. Check the config name.");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }



        root = FXMLLoader.load(getClass().getResource("WelcomePage.fxml"));
        stage = (Stage) ((Node) b.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public static List<File> unzipAll(File directory) throws IOException {
        List<File> extractedFiles = new ArrayList<>();


        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory: " + directory);
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".zip")) {
                extractedFiles.addAll(unzip(file.getAbsolutePath()));
            }
        }

        return extractedFiles;
    }

    public static List<File> unzip(String zipFilePath) throws IOException {
        List<File> extractedFiles = new ArrayList<>();
        Path zipFile = Paths.get(zipFilePath);
        String destDir = zipFile.getParent().toString();
        String zipFileName = zipFile.getFileName().toString();

        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = destDir + File.separator + zipFileName.replace(".zip", "") + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractedFiles.add(extractFile(zipIn, filePath));
                } else {
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }

        return extractedFiles;
    }

    private static File extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        Files.copy(zipIn, file.toPath());
        return file;
    }
}


}



