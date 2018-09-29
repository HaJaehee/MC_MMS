<?php
/*
* Plugin Name: Auto Refresh Single Page
*
* Description: Adds a little box for you to specify if a page should auto refresh after a given number of seconds.
*
* Company: Josh Kohlbach
* Author: Josh Kohlbach
* Author URI: http://www.codemyownroad.com
* Plugin URI: http://www.codemyownroad.com/products/auto-refresh-single-page
* Version: 1.1
*/


function arspAddRefreshOptionBox() {
	/* Add metabox for selector on pages */
	add_meta_box(
		'arspRefreshOptionsBox',
		'Auto Refresh Single Page',
		'arspRefreshOptionsBox',
		'page',
		'side',
		'low'
	);
	
	add_meta_box(
		'arspRefreshOptionsBox',
		'Auto Refresh Single Page',
		'arspRefreshOptionsBox',
		'post',
		'side',
		'low'
	);
	
}

function arspAddRefreshMetaTag() {
	global $wp_query;
	$page_obj = $wp_query->get_queried_object();
	
	if (!is_admin()) {
		$arsp_options = unserialize(get_post_meta($page_obj->ID, 'arsp_options', true));
		if (intval($arsp_options['seconds']) && intval($arsp_options['seconds'] > 0)) {
			echo '<meta http-equiv="refresh" content="' . $arsp_options['seconds'] . '" />';
		}
	}
}

function arspRefreshOptionsBox() {
	global $post;
	
	$arsp_options = unserialize(get_post_meta($post->ID, 'arsp_options', true));
	
	echo '<input type="hidden" name="arsp_noncename" id="arsp_noncename" value="' . wp_create_nonce( plugin_basename(__FILE__) ) . '" />';
	
	echo '<p><label>Seconds to refresh page after?:</label> <input type="text" name="arsp_options[seconds]" id="arsp_seconds" value="' . $arsp_options['seconds'] . '" style="width: 50px;" /></p>';
	echo '<p class="description">Blank or 0 for no refresh.</p>';
}

function arspSaveRefreshOptions($post_id) {
	global $post;
	
	if (!wp_verify_nonce($_POST["arsp_noncename"], plugin_basename(__FILE__)))
		return $post_id;
	
	 if ( !current_user_can( 'edit_post', $post_id ))
	 	 return $post_id;
	
	$arsp_options = $_POST['arsp_options'];
	
	// New, Update, and Delete
	
	if (empty($arsp_options)) {
		delete_post_meta(
			$post_id, 
			'arsp_options', 
			get_post_meta($post_id, 'arsp_options', true)
		);
		return;
	}
	
	if (!intval($arsp_options['seconds'])) {
		$arsp_options['seconds'] = 0;
	}
		
	if (!add_post_meta($post_id, 'arsp_options', serialize($arsp_options), true))
		update_post_meta($post_id, 'arsp_options', serialize($arsp_options));
	
}

/*******************************************************************************
** initRefreshSinglePage()
**
** Initialize the plugin
**
** @since 0.1
*******************************************************************************/
function initRefreshSinglePage() {
	/* Add options box to pages */
	add_action('add_meta_boxes', 'arspAddRefreshOptionBox');
	add_action('save_post', 'arspSaveRefreshOptions');
}

add_action('init', 'initRefreshSinglePage');
/* Add code to generated page if req. */
add_action('wp_head', 'arspAddRefreshMetaTag');

?>
