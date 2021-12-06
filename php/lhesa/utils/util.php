<?php
    // This is only for testing purposes to print all records from the database.
    header('Content-Type: application/json');
    function my_pretty_print($key, $json_string)
    {
        echo $key . ":" . json_encode($json_string, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    }
?>