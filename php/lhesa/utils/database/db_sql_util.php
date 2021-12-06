<?php    
    $db_config = require __DIR__ . '/db_config.php';
    mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
    try {
        $mysqli = new mysqli($db_config['host'], 
                            $db_config['user'], 
                            $db_config['pass'], 
                            $db_config['database']);

        // $mysqli->set_charset($db_config['charset']);
        $mysqli->options(MYSQLI_OPT_INT_AND_FLOAT_NATIVE, 1);
        return $mysqli;
    } catch (\mysqli_sql_exception $e) {
        throw new \mysqli_sql_exception($e->getMessage(), $e->getCode());
    } finally {
        unset($db_config);
    }

    function close_db(){
        global $mysqli;
        $mysqli->close();
    }

    function prepared_query($sql, $params, $types = "")
    {
        global $mysqli;
        $types = $types ?: str_repeat("s", count($params));
        $stmt = $mysqli->prepare($sql);
        $stmt->bind_param($types, ...$params);
        $stmt->execute();
        return $stmt;
    }

    function prepared_select($sql, $params = [], $types = "") {
        return prepared_query($sql, $params, $types)->get_result();
    }

    function fetch_all($sql){
        return prepared_select($sql)->fetch_all(MYSQLI_ASSOC);
    }

    function fetch_one($sql){
        return prepared_select($sql)->fetch_all(MYSQLI_ASSOC);
    }

?>