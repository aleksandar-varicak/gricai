<?php
require_once '../../classes/AutoloadClasses.inc';
SecurityChecker::sessionStart();
session_destroy();
header("Location: login.php");
