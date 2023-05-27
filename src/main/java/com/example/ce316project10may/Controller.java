package com.example.ce316project10may;

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
import java.awt.event.MouseEvent;
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
import java.nio.file.*;
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
    private TextField configName;
    @FXML
    private ChoiceBox<String> configurationBox = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> languageBox = new ChoiceBox<>();

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
    @FXML
    private TableView<ProjectDetail> OpenProjectTable = new TableView<>();

    @FXML
    private TableColumn<ProjectDetail, String> ExpectedOutputColumn = new TableColumn<>();

    @FXML
    private TableColumn<ProjectDetail, String> StudentOutputColumn = new TableColumn<>();

    @FXML
    private TableColumn<ProjectDetail, String> ProjectNameColumn = new TableColumn<>();
    @FXML
    private TableColumn<ProjectDetail, String> StudentIdColumn = new TableColumn<>();
    @FXML
    private TableColumn<ProjectDetail, String> ConfigNameColumn = new TableColumn<>();
    @FXML
    private TableColumn<ProjectDetail, String> studentResultCol = new TableColumn<>();
    @FXML
    private TextField configNameTxt1 = new TextField();
    @FXML
    private TableColumn<Configuration, String> editAttributeName1 = new TableColumn<>();

    @FXML
    private TableColumn<Configuration, String> editAttributeValue1 = new TableColumn<>();
    @FXML
    private TableView<Configuration> editTab2TW1 = new TableView<>();
    @FXML
    private Tab exportTab = new Tab();

    Configuration configuration = null;

    ArrayList<Configuration> configurations = new ArrayList<>();


private boolean hasError;
    public void compileAndExecuteWithParameter(List<File> files) throws InterruptedException, IOException {

        List<String> erroredFiles = new ArrayList<>();

        if (this.configuration != null) {
            String configurationName = this.configuration.getInput();
        } else {
            System.out.println("Configuration is not initialized yet.");
        }

        for (File file : files) {

            hasError = false;

            String studentOutputForError = "";

            try {
                String filePath = file.getAbsolutePath();

                ProcessBuilder compileProcessBuilder = null;
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

                if (compileProcess.exitValue() != 0) {
                    printProcessErrors(compileProcess);
                    studentOutputForError = "Compilation failed";
                    throw new RuntimeException("Compilation failed. Check the error stream for more details.");
                }

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


                // Check if the execution process was successful
                if (executeProcess.waitFor() != 0) {
                    printProcessErrors(executeProcess);
                    studentOutputForError = "Execution failed";
                    throw new RuntimeException("Execution failed. Check the error stream for more details.");

                } else {
                    //if there is no error
                    studentOutputForError = printProcessOutput(executeProcess);

                }

            } catch (RuntimeException | IOException e) {
                System.err.println("Error compiling/running file: " + file.getAbsolutePath());
                e.printStackTrace();
                erroredFiles.add(file.getAbsolutePath());
                this.hasError = true;
            }

            if (!erroredFiles.isEmpty()) {
                System.out.println("Files that encountered errors during compilation/execution:");
                for (String erroredFile : erroredFiles) {
                    System.out.println(erroredFile);
                }
            }

            if (hasError && studentOutputForError.isEmpty()) {
                studentOutputForError = "Error occurred but couldn't specify if during compilation or execution";
            }

            String filePathid = file.getAbsolutePath();
            int lastSeparatorIndex = filePathid.lastIndexOf(File.separator);
            int secondLastSeparatorIndex = filePathid.lastIndexOf(File.separator, lastSeparatorIndex - 1);
            String studentId = filePathid.substring(secondLastSeparatorIndex + 1, lastSeparatorIndex);

            System.out.println("student id: " + studentId);

            String projectName = projectname.getText();
            String studentSourceCodePath = filePathid;
            String expectedOutput2 = expectedOutputTxt.getText();
            String configname = configurationBox.getValue();
            saveProjectDetails(projectName, studentSourceCodePath);

            if (studentOutputForError == null) {
                studentOutputForError = "NULL";
            }


            //save to project table

            try {
                Class.forName("org.sqlite.JDBC");

                connection = DriverManager.getConnection("jdbc:sqlite:database.db");

                String query = "SELECT COUNT(*) FROM projects WHERE student_id = ? AND project_name = ? AND student_src = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, studentId);
                statement.setString(2, projectName);
                statement.setString(3, studentSourceCodePath);

                ResultSet resultSet = statement.executeQuery();
                int count = resultSet.getInt(1);
                resultSet.close();
                statement.close();

                if (count == 0) {
                    String insertProjectSQL = "INSERT INTO projects(project_name, student_src, student_output, expected_output, configuration_name, student_id) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmtProject = connection.prepareStatement(insertProjectSQL);
                    pstmtProject.setString(1, projectName);
                    pstmtProject.setString(2, studentSourceCodePath);
                    pstmtProject.setString(3, studentOutputForError);
                    pstmtProject.setString(4, expectedOutput2);
                    pstmtProject.setString(5, configname);
                    pstmtProject.setString(6, studentId);

                    pstmtProject.executeUpdate();
                    pstmtProject.close();

                    System.out.println("Record inserted.");
                } else {
                    System.out.println("The combination of student_id, project_name, and student_src already exists.");
                }

                connection.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //save to student table

            String studentResult;
            String expectedOutput = expectedOutputTxt.getText();


            if (studentOutputForError == null) {
                studentResult = "NULL";
            } else if (studentOutputForError.equals(expectedOutput)) {
                studentResult = "Success";
            }
            else if (hasError) {
                studentResult = studentOutputForError;  // If an error occurred, set studentResult to the error message
            } else {
                studentResult = "Failure";
            }


            try {
                Class.forName("org.sqlite.JDBC");

                connection = DriverManager.getConnection("jdbc:sqlite:database.db");

                String query = "SELECT COUNT(*) FROM " + "student" + " WHERE student_id = ? AND project_name = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, studentId);
                statement.setString(2, projectName);

                ResultSet resultSet = statement.executeQuery();
                int count = resultSet.getInt(1);
                resultSet.close();
                statement.close();

                if (count == 0) {
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

        executedResults();

    }

    public void loadConfigurationNames() {
        if (this.connection == null) {
            try {
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

            configurationBox.getItems().clear();

            ArrayList<String> configNames = new ArrayList<>();
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
            pstmt.setString(1, config);
            ResultSet rs = pstmt.executeQuery();

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


    public void saveProjectDetails(String projectName, String studentSourceCodePath) {

        String insertProjectSQL = "INSERT INTO projects(project_name, student_src) VALUES (?, ?)";
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
            PreparedStatement pstmt = connection.prepareStatement(insertSQL);
            pstmt.setString(1, configName);
            pstmt.setString(2, language);


            String result = String.join(" ", getArgumentsList());
            pstmt.setString(3, result);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static String extractedClassName;
    static String extractedStudentID;

    public List<File> chooseFile() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Select Project Directory");

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
                        extractedClassName = extractClassName(selectedFile);
                    }
                }
            }
        }
        return selectedFiles;
    }


    public void executeFiles(List<File> selectedFiles) {
        if (!selectedFiles.isEmpty()) {
            try {
                compileAndExecuteWithParameter(selectedFiles);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void executeAll(ActionEvent d) throws IOException {


        String project_name = projectname.getText();
        if (project_name == null || project_name.trim().isEmpty() || configurationBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Input Error");
            alert.setContentText("Please enter a project and config name to continue.");
            alert.showAndWait();
        } else {
            List<File> selectedFiles = chooseFile();
            executeFiles(selectedFiles);
        }
        filePathBox.clear();
        configurationBox.getSelectionModel().clearSelection();
        projectname.clear();
        expectedOutputTxt.clear();

        root = FXMLLoader.load(getClass().getResource("ListStudentResults.fxml"));

        stage = (Stage) ((Node) d.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


    }


    private String printProcessOutput(Process process) throws IOException {
        String finalOutput;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    output.append(System.lineSeparator());
                } else {
                    firstLine = false;
                }
                output.append(line.trim());
            }
            finalOutput = output.toString().trim();
            System.out.println(finalOutput);

        }
        return finalOutput;
    }

    private static void printProcessErrors(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }
        }
    }


    public void switchToOpenProject(ActionEvent a) throws IOException {
        root = FXMLLoader.load(getClass().getResource("OpenProject.fxml"));
        stage = (Stage) ((Node) a.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(true);
    }

    private Connection connection;

    private static boolean isSetupDone = false;

    public void createTable() {

        if (!isSetupDone) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement statement = connection.createStatement();
                String configSql = "CREATE TABLE IF NOT EXISTS configuration(" +
                        "configuration_language TEXT NOT NULL," +
                        "configuration_name TEXT PRIMARY KEY," +
                        "given_input TEXT)";
                statement.execute(configSql);
                System.out.println("Table config created successfully");

                String createTableSQL = "CREATE TABLE IF NOT EXISTS projects (" +
                        "project_name TEXT(50)  NOT NULL," +
                        "student_src TEXT(100) NOT NULL ," +
                        "student_output TEXT(100) NOT NULL," +
                        "expected_output TEXT(100) NOT NULL," +
                        "configuration_name TEXT," +
                        "student_id VARCHAR(50)  NOT NULL," +
                        "FOREIGN KEY (configuration_name) REFERENCES configuration(configuration_name)," +
                        "FOREIGN KEY (project_name, student_id) REFERENCES student(project_name, student_id)" +
                        ")";
                statement.execute(createTableSQL);
                System.out.println("Table createTableSQL created successfully");

                String studentTable = "CREATE TABLE IF NOT EXISTS student (" +
                        "student_id VARCHAR(50)  NOT NULL," +
                        "student_result BOOLEAN NOT NULL," +
                        "project_name TEXT(50) NOT NULL," +
                        "PRIMARY KEY (student_id, project_name)" +
                        ")";
                statement.execute(studentTable);
                System.out.println("Table studentTable created successfully");
                isSetupDone = true;

                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void loadProjectNames() {
        String sql = "SELECT DISTINCT project_name FROM projects";

        try {
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            projectNameCB.getItems().clear();

            ArrayList<String> projectNames = new ArrayList<>();
            while (rs.next()) {
                String projectName = rs.getString("project_name");
                projectNames.add(projectName);
            }
            projectNameCB.getItems().addAll(projectNames);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private ObservableList<ProjectDetail> projectDetails = FXCollections.observableArrayList();

    public void loadProjectDetails(String projectName) {

        projectDetails.clear();

        String query = "SELECT p.student_id, p.configuration_name, p.student_output, p.expected_output, s.student_result " +
                "FROM projects p " +
                "INNER JOIN student s ON p.student_id = s.student_id AND p.project_name = s.project_name " +
                "WHERE p.project_name = ?";

        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, projectName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                projectDetails.add(new ProjectDetail(projectName, rs.getString("student_id"), rs.getString("configuration_name"), rs.getString("student_output")
                        , rs.getString("expected_output"), rs.getString("student_result")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        OpenProjectTable.setItems(projectDetails);
    }

    //Req2 import and export
    public void goToExportConfig() {

        String url = "jdbc:sqlite:database.db";

        if (configNameTxt1.getText().isEmpty()) {
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

            editAttributeName1.setCellValueFactory(new PropertyValueFactory<Configuration, String>("X"));
            editAttributeValue1.setCellValueFactory(new PropertyValueFactory<Configuration, String>("Y"));
            editTab2TW1.setItems(list2);


            List<String> columnValues = new ArrayList<>();


            String configName = configNameTxt1.getText();
            String sql2 = "SELECT * FROM configuration WHERE configuration_name = '" + configName + "'";


            ResultSet rs2 = stmt.executeQuery(sql2);

            while (rs2.next()) {

                for (int i = 1; i <= numColumns; i++) {


                    String columnName = metaData.getColumnName(i);
                    allColumnNames.add(columnName);

                    String columnValue = rs2.getString(i);
                    columnValues.add(columnValue);


                    Configuration temp2 = new Configuration(columnName, columnValue);

                    editTab2TW1.getItems().add(temp2);


                }
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        TabPane.getSelectionModel().select(exportTab);

    }

    public void exportConfig() throws IOException {

        FileChooser fileChooser = new FileChooser();
        MouseEvent mouseEvent = null;
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (int i = 1; i < editTab2TW1.getItems().size(); i++) {
                    String attrName = editTab2TW1.getItems().get(i).getX();
                    String attrValue = editTab2TW1.getItems().get(i).getY();
                    writer.write(attrName + ": " + attrValue);
                    writer.newLine();
                }
                System.out.println("Configurations exported successfully!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText(null);
                alert.setContentText("Configurations exported successfully!");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error exporting configurations: " + e.getMessage());
            }
        }

    }

    public void importConfigurations() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String configurationLanguage = null;
                String configurationName = null;
                String givenInput = null;

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        if (key.equalsIgnoreCase("configuration_language")) {
                            configurationLanguage = value;
                        } else if (key.equalsIgnoreCase("configuration_name")) {
                            configurationName = value;
                        } else if (key.equalsIgnoreCase("given_input")) {
                            givenInput = value;
                        }
                    } else {
                        System.out.println("Invalid line: " + line);
                    }
                }

                if (configurationLanguage != null && configurationName != null && givenInput != null) {
                    insertConfiguration(configurationLanguage, configurationName, givenInput);
                    System.out.println("Configurations imported successfully!");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Export Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Configurations imported successfully!");
                    alert.showAndWait();
                } else {
                    System.out.println("Incomplete configuration data.");
                }

            } catch (IOException | SQLException e) {
                e.printStackTrace();
                System.out.println("Error importing configurations: " + e.getMessage());
            }
        } else {
            System.out.println("No file selected.");
        }
    }

    public void insertConfiguration(String language, String configName, String input) throws SQLException {

        String url = "jdbc:sqlite:database.db";
        String insertSql = "INSERT INTO configuration (configuration_language, configuration_name, given_input) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(insertSql)) {

            statement.setString(1, language);
            statement.setString(2, configName);
            statement.setString(3, input);
            statement.executeUpdate();

        }
    }

        @Override
        public void initialize (URL location, ResourceBundle resources){

            createTable();
            languageBox.getItems().addAll("Java", "C", "Python");
            loadConfigurationNames();
            columnNameTextField.getItems().addAll("configuration_name", "given_input");

            loadConfigurationNames();
            createTable();
            ListConfig();
            ListStudentResults();
            loadProjectNames();

            ProjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));
            StudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
            ConfigNameColumn.setCellValueFactory(new PropertyValueFactory<>("configName"));
            StudentOutputColumn.setCellValueFactory(new PropertyValueFactory<>("studentOutput"));
            ExpectedOutputColumn.setCellValueFactory(new PropertyValueFactory<>("expectedOutput"));
            studentResultCol.setCellValueFactory(new PropertyValueFactory<>("studentResult"));


            projectNameCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadProjectDetails(newValue);
                }
            });

            // projectNameCB

            try {

                Class.forName("org.sqlite.JDBC");

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

        public void switchToResults (ActionEvent a) throws IOException {
            root = FXMLLoader.load(getClass().getResource("ListStudentResults.fxml"));
            stage = (Stage) ((Node) a.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setResizable(true);
        }


        public File unzipFile (File file) throws IOException {
            // Create a temporary directory for unzipping the contents
            Path tempDir = Files.createTempDirectory("unzippedFiles");

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    File outputFile = tempDir.resolve(entry.getName()).toFile();
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
            }

            // Select the Main.java file from the unzipped files
            File mainFile = tempDir.resolve("Main.java").toFile();
            if (mainFile.exists()) {
                return mainFile;
            } else {
                throw new FileNotFoundException("Main.java not found in the unzipped files.");
            }
        }

        public void createConfiguration () {
            if (configName.getText().isEmpty() || languageBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a configuration name and select a language.");
                alert.showAndWait();
            } else {
                setCommands(languageBox.getValue(), extractedClassName, configName.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Configuration created successfully.");
                alert.showAndWait();
            }
            configName.clear();
            inputTxt.clear();
        }

        private String extractClassName (File file){
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


    public void openFileChooserAndReadFile() {
        List<Integer> numbers = new ArrayList<>();
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);


        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())));
                numbers = Arrays.stream(content.split("\\s+"))
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                inputTxt.setText(content);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Error reading the file");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    public void openFileChooserAndReadFiles() {
        List<Integer> numbers = new ArrayList<>();
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())));
                numbers = Arrays.stream(content.split("\\s+"))
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                expectedOutputTxt.setText(content);
            } catch (IOException e) {
            }
        }
    }

        @FXML
        public void switchToConfig (ActionEvent e) throws IOException {

            root = FXMLLoader.load(getClass().getResource("Configuration.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setResizable(true);
        }

        @FXML
        public void switchToNewProject (ActionEvent e) throws IOException {

            root = FXMLLoader.load(getClass().getResource("NewProject.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setResizable(true);
        }

        public void switchToEditConfig (ActionEvent a) throws IOException {
            root = FXMLLoader.load(getClass().getResource("editPage.fxml"));
            stage = (Stage) ((Node) a.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setResizable(true);
        }

        public void goToEditConfig () {

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


        public void ListStudentResults () {

            try {

                Class.forName("org.sqlite.JDBC");


                String url = "jdbc:sqlite:database.db";
                Connection connection = DriverManager.getConnection(url);


                String projectName = projectNameCB.getValue();

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

        public void executedResults () {
            try {

                Class.forName("org.sqlite.JDBC");


                String url = "jdbc:sqlite:database.db";
                Connection connection = DriverManager.getConnection(url);


                String projectName = projectname.getText();

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

        public void editMethod (ActionEvent b) throws IOException {


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
                    System.out.println("Update failed. No rows affected.");
                }

                preparedStatement.close();
                connection.close();

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void deleteMethod (ActionEvent b) throws IOException {
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

        //edit first tab table view
        public void ListConfig () {
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

        public void switchToWelcome (ActionEvent a) throws IOException {
            root = FXMLLoader.load(getClass().getResource("WelcomePage.fxml"));
            stage = (Stage) ((Node) a.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setResizable(true);
        }
        public static List<File> unzipAll (File directory) throws IOException {
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

        public static List<File> unzip (String zipFilePath) throws IOException {
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

        private static File extractFile (ZipInputStream zipIn, String filePath) throws IOException {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            Files.copy(zipIn, file.toPath());
            return file;
        }
    public void showGuideCreateConfig() {
        Alert guideAlert = new Alert(Alert.AlertType.INFORMATION);
        guideAlert.getDialogPane().setPrefWidth(600);
        guideAlert.getDialogPane().setPrefHeight(300);
        guideAlert.setTitle("Guide");
        guideAlert.setHeaderText("How to Create Configuration");
        guideAlert.setContentText("1. Choose a programming language from the choice box.\n2. Give configuration a name\n3. To add an input you can either select a txt file or enter them manually. The inputs should be separated by space in a single line for both cases.\n Select one of the option and skip the given space for other option.\n4.Click on create configuration button.\n*If you want to import an existing configuration skip all the steps above and click on 'Import Configuration'. Select the file that has configuration information.");
        guideAlert.showAndWait();
    }
    public void showGuideCreateProject() {
        Alert guideAlert = new Alert(Alert.AlertType.INFORMATION);
        guideAlert.getDialogPane().setPrefWidth(600);
        guideAlert.getDialogPane().setPrefHeight(300);
        guideAlert.setTitle("Guide");
        guideAlert.setHeaderText("How to Create New Project");
        guideAlert.setContentText("1. Give project a name. \n2. Select the name of the configuration you want to include to the project or you can create a new configuration to use.\n3. To provide the expected output you can either select a txt file or enter them manually. The output should be separated by space in a single line for both cases.\n Select one of the option and skip the given space for other option.\n4.Click on 'Select Student Files/Run' button.\n5. Select the folder that contains the zip files of the students. All files should be in zip format!");
        guideAlert.showAndWait();
    }
    public void showGuideListResults() {
        Alert guideAlert = new Alert(Alert.AlertType.INFORMATION);
        guideAlert.getDialogPane().setPrefWidth(600);
        guideAlert.getDialogPane().setPrefHeight(200);
        guideAlert.setTitle("Guide");
        guideAlert.setHeaderText("How to See The Student Results");
        guideAlert.setContentText("1. Select the name of the project that you want to display the student results \n2. Click on 'See Results'. The results of the students withe the student IDs will be displayed in the table.");
        guideAlert.showAndWait();
    }
    public void showGuideOpenProject() {
        Alert guideAlert = new Alert(Alert.AlertType.INFORMATION);
        guideAlert.getDialogPane().setPrefWidth(600);
        guideAlert.getDialogPane().setPrefHeight(200);
        guideAlert.setTitle("Guide");
        guideAlert.setHeaderText("How to Open An Existing Project");
        guideAlert.setContentText("1. Select the name of the project that you want to open from the choice box.All the information about the project will be displayed in the table below.");
        guideAlert.showAndWait();
    }
    public void showGuideEdit() {
        Alert guideAlert = new Alert(Alert.AlertType.INFORMATION);
        guideAlert.getDialogPane().setPrefWidth(600);
        guideAlert.getDialogPane().setPrefHeight(400);
        guideAlert.setTitle("Guide");
        guideAlert.setHeaderText("How to Edit/Export Configuration");
        guideAlert.setContentText("The table on the left contains the information about the existing configurations.\nYou can either enter the name of the configuration to edit or export. Please select one and leave the name text field of the other option empty. \nCheck from the table and enter the name of the configuration you want to edit/export. \nClick on 'Edit Configuration' to edit or 'Export Configuration' to export. You will be directed to the next step!\n**TO EDIT** \n1. To delete the chosen configuration click on 'Delete Configuration' button \n2. To edit the configuration select the column you want to change value of. \n3. Enter a new value for the column and click on 'Edit'.\n**TO EXPORT**\n1. Click on 'Export Configuration' button to export the chosen configuration. The configuration will be saved on your computer!" );
        guideAlert.showAndWait();
    }


}
