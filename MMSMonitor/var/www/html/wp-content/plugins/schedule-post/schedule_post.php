<?php
/**
 * Plugin Name: Schedule Post
 * Plugin URI: http://djken2006.googlepages.com/
 * Description: Schedule Post plugin automatically publish post at regular intervals. 
 * Version: 1.0
 * Author: Djken
 * Author URI: http://djken2006.googlepages.com/
 */

/*
Copyright 2008  Djken

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

add_action('admin_menu', 'sp_add_option_page');
add_action('sp_add_post_event', 'sp_add_post');

register_activation_hook(__FILE__, 'sp_activation');  //It seems cannot work in Win32
register_deactivation_hook(__FILE__, 'sp_deactivation');  //It seems cannot work in Win32

/**
 * The function is called when the plugin is deactived.
 * It first clear the cron job of publishing post, then
 * remove the option stored in database and remove the
 * action hook.
 */
function sp_deactivation() {
	sp_clear_schedule();
	delete_option('sp_option');
	remove_action('sp_add_post_event', 'sp_add_post');
}

/**
 * The function will be called when the plugin is actived.
 * It sets the default option and then start the cron job
 * to publish the post automatically.
 */
function sp_activation() {
	//have not set the option before, set it's default value
	$sp_option = array(
	'path' => ABSPATH. 'mypost',
	'interval' => 12,  //default inerval of publishing a post is 12 hours
	'author' => 1,  //default author id is 1
	'status' => 'published', //default status of the post
	'lasttime' => time()  //schedule the event from now on
	);

	add_option('sp_option', $sp_option);

	sp_update_schedule();  //start this cron job
}

function sp_add_option_page() {
	add_options_page('Schedule Post Option', 'Schedule Post', 8, __FILE__, 'sp_option_page');
}

function sp_option_page() {

	$sp_option = get_option('sp_option');

	if ($_POST['update'] == 'Y') {
		$new_interval = intval($_POST['interval']);
		if ($sp_option['interval'] != $new_interval) {
			sp_clear_schedule();  //clear the scheduled event first
			sp_update_schedule();  //reschedule the post event using the new option
			$sp_option['interval'] = $new_interval;
		}

		$sp_option['path'] = stripslashes(trim($_POST['path']));
		$sp_option['author'] = $_POST['author'];
		$sp_option['status'] = $_POST['status'];

		update_option('sp_option', $sp_option);
		echo '<div class="updated"><p>Settings are saved successfully!</p></div>';
	}
?>

<div class="wrap">
	<h2>Schedule Post Options</h2>
	<p>Schedule Post plugin automatically publish post at regular intervals. It read text files from special path and parse it into posts.</p>
	
	<form method="POST">
	<input type="hidden" name="update" value="Y">	
	
	<p>Enter the path of the folder you would like to post the files from: (<b>e.g.</b> <i>/home/myuser/mydomain.com/wp-content/myposts</i>)
	<input type="text" size="40" name="path" value="<?php echo $sp_option['path'];?>"/></p>
	
	<p>Interval of publishing a post		
	<select name="interval">
		<option value="6" <?php echo $sp_option['interval']==1?'selected':'';?>>6 hours</option>	
		<option value="12" <?php echo $sp_option['interval']==12?'selected':'';?>>12 hours</option>
		<option value="24" <?php echo $sp_option['interval']==24?'selected':'';?>>one day</option>
		<option value="48" <?php echo $sp_option['interval']==48?'selected':'';?>>two days</option>	
		<option value="72" <?php echo $sp_option['interval']==72?'selected':'';?>>three days</option>	
		<option value="168" <?php echo $sp_option['interval']==168?'selected':'';?>>one week</option>	
	</select>
	</p>
	
	<p><label for="author">The author of the post</label>	
	<?php	
	$users = get_users_of_blog();
	echo '<select id="author" name="author">';
	foreach ($users as $user) {
		echo "<option value={$user->user_id} " . ($sp_option['author'] == $user->user_id?'selected':'') .">{$user->display_name}</option>";
	}
	echo '</select>';
	?>
	</p>	
	
	<p><label for="status">The status of the post</label>
	<select id="status" name="status">
		<option value="publish" <?php echo $sp_option['status']=='publish'?'selected':'' ?>>publish</option>
		<option value="draft" <?php echo $sp_option['status']=='draft'?'selected':'' ?>>draft</option>
	</select></p>
	
	<p><input type="submit" value="Update"></p>
	</form>	
</div>

<?php
}

function sp_clear_schedule() {
	wp_clear_scheduled_hook('sp_add_post_event');
}

function sp_update_schedule() {
	if (!wp_next_scheduled('sp_add_post_event')) {
		wp_schedule_event(time(), 'hourly', 'sp_add_post_event');
	}
}

/**
 * This function is fire every hour, if it's time to publish
 * a new post, we read the next txt file from the special path
 * and publish the content of it as a new post. 
 */
function sp_add_post() {
	$sp_option = get_option('sp_option');

	//check whether it is time to publish a new post
	if (empty($sp_option['lasttime']) || $sp_option['lasttime'] + $sp_option['interval'] * 60 * 60 <= time()) {

		$post = sp_read_next_post($sp_option['path']);
		if  (!empty ($post) ) {print_r($post);
		if (wp_insert_post($post)) {  //publish a new post

			$sp_option['lasttime'] = time();  //update time counter
			update_option('sp_option', $sp_option);
		}
		}

	}
}

/**
 * Read the next file in the path, then parse its content and
 * rename the suffix to .post in case it will be published again
 */
function sp_read_next_post($path) {
	if (!($d = opendir($path))) {
		return false;
	}
	else {
		while (($f = readdir($d)) !== false ) {
			$file_name = $path . '/' . $f;
			//only read those file with suffix .txt
			if ($f == '.' || $f == '..' || is_dir($file_name) || strtolower(substr($f, -4)) != '.txt')
			continue;

			$text = file_get_contents($file_name);

			//rename the file to .post so that it won't be post again next time
			$new_name = substr($file_name, 0, -4) . '.post';
			rename($file_name, $new_name);

			return sp_parse_content($text);
		}
	}
}

/**
 * Parse the text into a post. The first line is regard as title,
 * and the rest is the content of a post. One line is a paragraph
 * @return the array of the post in wordpress or false when the text
 * cannot be parsed correcttly.
 */
function sp_parse_content($text) {
	$sp_option = get_option('sp_option');

	$text_array = explode("\n", $text);
	if (count($text_array) < 2) return false; //make sure there are at least two lines, the first is title, the rest is content

	$title = trim(array_shift($text_array)); //regard the first line as title of the post
	array_map('sp_trim_line', $text_array); //the rest is the content of the post, surround each line with <p> and </p>
	$content = implode(null, $text_array);

	if (empty($title) || empty($content)) {
		return false;
	}

	//get other fields from the option array
	$author = $sp_option['author'];
	$status = $sp_option['status'];

	return array(
	'post_title' => $title,
	'post_content' => addslashes($content),
	'post_status' => $status,
	'post_author' => $author
	);

}

function sp_trim_line($line) {
	return '<p>' . trim($line) . '</p>';
}
?>