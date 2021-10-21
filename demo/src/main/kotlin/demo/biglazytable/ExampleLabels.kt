package demo.biglazytable

import model.ILabel

enum class ExampleLabels(val deutsch : String, val english : String) : ILabel {
    TITLE                   ("Personalien",                 "Personal Data"),
    HEADERGROUP             ("Informationsübersicht",       "Information"),
    GROUPTITLEPERSINFO      ("Persönliche Information",     "Personal Information"),
    GROUPTITLEADDRESS       ("Adresse",                     "Address"),
    GROUPTITLEBUSINESSINFO  ("Geschäftliche Informationen", "Business Information"),

    //*********************************
    //Personal information

    FIRSTNAME               ("Vorname",         "First name"),
    LASTNAME                ("Nachname",        "Last name"),
    GENDER                  ("Geschlecht",      "Gender"),
    SELECTIONMAN            ("Mann",            "Man"),
    SELECTIONWOMAN          ("Frau",            "Woman"),
    SELECTIONOTHER          ("Anderes",         "Other"),
    MARRIED                 ("Verheiratet",     "Married"),
    SELECTIONYES            ("Ja",              "Yes"),
    SELECTIONNO             ("Nein",            "No"),
    AGE                     ("Alter",           "Age"),
    BIRTHDATE               ("Geburtstag",             "Birthdate"),
    HEIGHT                  ("Körper-Grösse",   "Body Height"),
    IMAGE_URL               ("Bild-Url",        "Image Url"),
    LASTOCCUPATION          ("Letzter Beruf",   "Last Occupation"),
    TAXNUMBER               ("Steuer-Nummer",   "Tax Number"),

    //*********************************
    //Adress infos

    POSTCODE    ("Postleitzahl",    "Postcode"),
    PLACE       ("Ort",             "Town/City"),
    STREET      ("Strasse",         "Street"),
    HOUSENUMBER ("Hausnummer",      "House Number"),

    //*********************************
    //Business details

    STATE               ("Status",              "State"),
    SELECTIONNORMAL     ("Normale Position",    "Normal Position"),
    SELECTIONKADER      ("Kader Position",      "Kader Position"),
    CONTRACTSTART       ("Vertrags-Start",      "Start of Contract"),
    WAGE                ("Lohn",                "Wage"),
    DEPARTMENT          ("Abteilung",           "Department"),
    EMPOYMENTTITLE      ("Beschäftigungstitel", "Employment title"),
    CONTRACTWASSIGNED   ("Vertrags-Unterschrift vorhanden", "Contract was signed"),



    //*********************************
    //additional attributes

    ID                  ("ID",              "ID"),


    //Validators
    SELECTIONVALIDATIONMSG("Nur eine Selektion möglich", "Only 1 selection possible."),
    FLOATINGPOINTVALIDATIONMSG("Zu viele Nachkommastellen","Too many decimal places."),
    REGEXVALIDATIONMSG("Eingabe muss zwischen 3 und 5 Zeichen lang sein", "The input must be 3 - 5 characters long"),
    REQUIREDVALIDATIONMSG("Eingabe erforderlich.", "Input required."),
    SYNTAXVALIDATIONMSG("Das ist nicht der korrekte Typ.", "This is not the correct input type."),
    DUALVALIDATIONMSG("Es muss eine der beiden Optionen ausgewählt werden.","You must choose one of the two given options."),

    DATEVALIDATOR("Du muss ein korrektes Datum eingeben.", "You must enter a correct date.")
    ;
}