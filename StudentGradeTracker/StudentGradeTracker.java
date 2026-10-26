
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

class Student {
    String name;
    int[] marks;
    int total;
    double percentage;
    String grade;

    Student(String name, int[] marks) {
        this.name = name;
        this.marks = marks;

        for (int mark : marks) {
            total += mark;
        }

        percentage = total / (double) marks.length;

        if (percentage >= 90) {
            grade = "A+";
        } else if (percentage >= 80) {
            grade = "A";
        } else if (percentage >= 70) {
            grade = "B";
        } else if (percentage >= 60) {
            grade = "C";
        } else if (percentage >= 40) {
            grade = "D";
        } else {
            grade = "F";
        }
    }

    void display() {
        System.out.println("\nStudent Name: " + name);

        for (int i = 0; i < marks.length; i++) {
            System.out.println("Subject " + (i + 1)
                    + " Marks: " + marks[i]);
        }

        System.out.println("Total: " + total + "/500");
        System.out.printf("Percentage: %.2f%%\n", percentage);
        System.out.println("Grade: " + grade);
        System.out.println("----------------------------");
    }
}

public class StudentGradeTracker {

    static Scanner sc = new Scanner(System.in);
    static ArrayList<Student> students = new ArrayList<>();
    static final String FILE_NAME = "students.txt";

    // Load saved records when the program starts
    static void loadStudents() {
        students.clear();

        try (BufferedReader br =
                     new BufferedReader(new FileReader(FILE_NAME))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|", -1);

                if (data.length != 6) {
                    System.out.println("Skipping invalid record.");
                    continue;
                }

                try {
                    String name = data[0];
                    int[] marks = new int[5];

                    for (int i = 0; i < 5; i++) {
                        marks[i] = Integer.parseInt(data[i + 1]);

                        if (marks[i] < 0 || marks[i] > 100) {
                            throw new NumberFormatException();
                        }
                    }

                    students.add(new Student(name, marks));

                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid record.");
                }
            }

        } catch (IOException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.out.println("Could not load saved records.");
            }
        }

        System.out.println("Loaded " + students.size()
                + " student record(s).");
    }

    // Save all student records to a file
    static boolean saveStudents() {
        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(FILE_NAME))) {

            for (Student student : students) {
                writer.print(student.name);

                for (int mark : student.marks) {
                    writer.print("|" + mark);
                }

                writer.println();
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error saving records: " + e.getMessage());
            return false;
        }
    }

    static void addStudent() {
        sc.nextLine();

        System.out.print("Enter student name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty() || name.contains("|")) {
            System.out.println("Invalid name. Please try again.");
            return;
        }

        int[] marks = new int[5];

        for (int i = 0; i < marks.length; i++) {
            System.out.print("Enter marks for subject "
                    + (i + 1) + " (0-100): ");

            while (true) {
                if (sc.hasNextInt()) {
                    int mark = sc.nextInt();

                    if (mark >= 0 && mark <= 100) {
                        marks[i] = mark;
                        break;
                    }
                } else {
                    sc.next();
                }

                System.out.print(
                        "Invalid input! Enter marks (0-100): ");
            }
        }

        students.add(new Student(name, marks));

        if (saveStudents()) {
            System.out.println(
                    "Student added and saved successfully!");
        } else {
            students.remove(students.size() - 1);
            System.out.println("Student was not saved.");
        }
    }

    static void viewStudents() {
        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }

        System.out.println("\n===== ALL STUDENT RECORDS =====");

        for (Student student : students) {
            student.display();
        }
    }

    // Search by student name, ignoring uppercase/lowercase
    static void searchStudent() {
        sc.nextLine();

        System.out.print("Enter student name to search: ");
        String searchName = sc.nextLine().trim();

        boolean found = false;

        for (Student student : students) {
            if (student.name.equalsIgnoreCase(searchName)) {
                student.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("Student not found.");
        }
    }

    static void showClassAverage() {
        if (students.isEmpty()) {
            System.out.println("No student records available.");
            return;
        }

        double totalPercentage = 0;

        for (Student student : students) {
            totalPercentage += student.percentage;
        }

        double average = totalPercentage / students.size();

        System.out.printf("Class Average: %.2f%%\n", average);
    }

    static void showHighestLowest() {
        if (students.isEmpty()) {
            System.out.println("No student records available.");
            return;
        }

        Student highest = students.get(0);
        Student lowest = students.get(0);

        for (Student student : students) {
            if (student.percentage > highest.percentage) {
                highest = student;
            }

            if (student.percentage < lowest.percentage) {
                lowest = student;
            }
        }

        System.out.println("Highest Scoring Student:");
        System.out.println(highest.name + " - "
                + highest.percentage + "%");

        System.out.println("Lowest Scoring Student:");
        System.out.println(lowest.name + " - "
                + lowest.percentage + "%");
    }

    public static void main(String[] args) {

        loadStudents();

        int choice;

        do {
            System.out.println("\n================================");
            System.out.println("     STUDENT GRADE TRACKER");
            System.out.println("================================");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Show Class Average");
            System.out.println("5. Show Highest and Lowest");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            while (!sc.hasNextInt()) {
                System.out.print("Please enter a number (1-6): ");
                sc.next();
            }

            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addStudent();
                    break;

                case 2:
                    viewStudents();
                    break;

                case 3:
                    searchStudent();
                    break;

                case 4:
                    showClassAverage();
                    break;

                case 5:
                    showHighestLowest();
                    break;

                case 6:
                    System.out.println("Thank you for using Student Grade Tracker!");
                    break;

                default:
                    System.out.println("Invalid choice! Select 1-6.");
            }

        } while (choice != 6);

        sc.close();
    }
}
