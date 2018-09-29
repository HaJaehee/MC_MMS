<?php
function wp_cache_flush() {
    global $wp_object_cache;

    return $wp_object_cache->flush();
}

?>
