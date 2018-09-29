<?php

add_action( 'wp_enqueue_scripts', 'one_edge_enqueue_styles' );
function one_edge_enqueue_styles() {
	wp_enqueue_style( 'one_edge-font', '//fonts.googleapis.com/css?family=Cabin:400,600|Open+Sans:400,300,600|Ubuntu:400,300,500,700');
	wp_enqueue_style( 'one_edge-style',  get_template_directory_uri() . '/style.css', array('llorix-one-lite-bootstrap-style'),'1.0.0');
	wp_enqueue_script( 'one_edge-custom-js', get_stylesheet_directory_uri() . '/js/custom-js.js', array('jquery'), '1.0.0', true );
}

function one_edge_setup() {

	/* Set the image size by cropping the image */
	add_image_size( 'one-edge-post-thumbnail-big', 340, 340, true );
	add_image_size( 'one_edge-post-thumbnail-mobile', 233, 233, true );

}
add_action( 'after_setup_theme', 'one_edge_setup', 11 );

/**
 * my custom login logo
 */
function mms_login_logo() { ?>
    <style type="text/css">
        body.login div#login h1 a {
            background-image: url(<?php echo get_bloginfo( 'template_directory' ) ?>/images/envelope.png);
            padding-bottom: 30px;
        }
    </style>
<?php }
add_action( 'login_enqueue_scripts', 'mms_login_logo' );

/**
 * login logo link
 */
function mms_login_logo_url() {
    return get_bloginfo( 'url' );
}
add_filter( 'login_headerurl', 'mms_login_logo_url' );

function mms_login_stylesheet() { ?>
    <link rel="stylesheet" id="mms_wp_admin_css"  href="<?php echo get_bloginfo( 'stylesheet_directory' ) . '/mms-login.css'; ?>" type="text/css" media="all" />
<?php }
add_action( 'login_enqueue_scripts', 'mms_login_stylesheet' );

/**
 * login logo title
 */
function mms_login_logo_url_title() {
    return 'MMS Monitoring Service';
}
add_filter( 'login_headertitle', 'mms_login_logo_url_title' );