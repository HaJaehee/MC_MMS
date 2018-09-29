<?php
/*
Plugin Name: Llorix One Companion
Plugin URI: https://github.com/Codeinwp/llorix-one-companion
Description: Add Our team, Our Services and Testimonials sections to Llorix One Lite theme.
Version: 1.1.4
Author: Themeisle
Author URI: http://themeisle.com
Text Domain: llorix-one-companion
Domain Path: /languages
License: GPLv2
License URI: https://www.gnu.org/licenses/gpl-2.0.html
*/

if ( ! function_exists( 'add_action' ) ) {
	die( 'Nothing to do...' );
}

/* Important constants */
define( 'LLORIX_ONE_COMPANION_VERSION', '1.1.4' );
define( 'LLORIX_ONE_COMPANION_URL', plugin_dir_url( __FILE__ ) );
define( 'LLORIX_ONE_COMPANION_PATH', plugin_dir_path( __FILE__ ) );

/**
 * Require section translations
 */
require LLORIX_ONE_COMPANION_PATH . 'inc/translations/general.php';

/* Required helper functions */
include_once( dirname( __FILE__ ) . '/inc/settings.php' );


/* Add new sections in Llorix One */
function llorix_one_companion_sections() {
	return array(
		'sections/llorix_one_lite_logos_section',
		'our-services-section',
		'sections/llorix_one_lite_our_story_section',
		'our-team-section',
		'happy-customers-section',
		'sections/llorix_one_lite_content_section',
		'sections/llorix_one_lite_ribbon_section',
		'sections/llorix_one_lite_latest_news_section',
		'sections/llorix_one_lite_contact_info_section',
		'sections/llorix_one_lite_map_section'
	);
}

/**
 * Load plugin textdomain.
 *
 * @since 1.0.0
 */

add_action( 'plugins_loaded', 'llorix_one_companion_load_textdomain' );

function llorix_one_companion_load_textdomain() {
	load_plugin_textdomain( 'llorix-one-companion', false, dirname( plugin_basename( __FILE__ ) ) . '/languages' );
	
	add_filter('llorix_one_companion_sections_filter', 'llorix_one_companion_sections');
}

/* Check if Llorix One theme is activated */

if ( ! empty ( $GLOBALS['pagenow'] ) && 'plugins.php' === $GLOBALS['pagenow'] ) {	
    add_action( 'admin_notices', 'llorix_one_companion_admin_notices', 0 );
}	

function llorix_one_companion_requirements() {
	
	$llorix_one_companion_errors = array();
	$theme = wp_get_theme();
	
	if ( ('Llorix One Lite' != $theme->name) && ('llorix-one-lite' !== $theme->template ) ) {

		$llorix_one_companion_errors[] = __( 'You need to have <a href="https://wordpress.org/themes/llorix-one-lite/" target="_blank">Llorix One Lite</a> theme in order to use Llorix One Companion plugin.','llorix-one-companion' );
	}

	return $llorix_one_companion_errors;

}

function llorix_one_companion_admin_notices()
{

    $llorix_one_companion_errors = llorix_one_companion_requirements();

    if ( empty ( $llorix_one_companion_errors ) )
        return;

    /* Suppress "Plugin activated" notice. */
    unset( $_GET['activate'] );
	
	echo '<div class="notice error my-acf-notice is-dismissible">';
		echo '<p>'.join($llorix_one_companion_errors).'</p>';
        echo '<p>'.__( '<i>Llorix One Companion</i> has been deactivated.', 'llorix-one-companion' ).'</p>';
    echo '</div>';

    deactivate_plugins( plugin_basename( __FILE__ ) );
}

/* Register style sheet. */
add_action( 'wp_enqueue_scripts', 'llorix_one_companion_register_plugin_styles' );

function llorix_one_companion_register_plugin_styles() {
	
	wp_enqueue_style( 'llorix-one-companion-style', LLORIX_ONE_COMPANION_URL.'css/style.css' );
	
}
