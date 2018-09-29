<?php
if (function_exists('load_plugin_textdomain')) {
    load_plugin_textdomain('include-me', false, 'include-me/languages');
}

if (isset($_POST['_wpnonce']) && wp_verify_nonce($_POST['_wpnonce'], 'save')) {
    if (isset($_POST['save'])) {
        if (isset($_POST['options'])) {
            $options = stripslashes_deep($_POST['options']);
            update_option('includeme', $options);
        } else {
            update_option('includeme', array());
        }
    }

    if (isset($_POST['find'])) {
        global $wpdb;
        $posts = $wpdb->get_results("select id, post_title from " . $wpdb->prefix . "posts where post_content like '%[includeme%' and post_type in ('post', 'page')");
    }
} else {
    $options = get_option('includeme', array());
}
?>
<div class="wrap">

    <h2>Include Me</h2>

    <p>
        Are you profitably using this plugin? 
        <a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=5PHGDGNHAYLJ8" target="_blank"><img style="vertical-align: bottom" src="http://www.satollo.net/images/donate.png"></a>
        <a href="http://www.satollo.net/donations" target="_blank">Even <b>2$</b> help: read more</a>
    </p>

    <p>
        <?php _e('Read <a href="http://www.satollo.net/plugins/include-me" target="_blank">the official documentation</a> on how to use the short tag [includeme] in your posts or pages.', 'header-footer') ?>
    </p>

    <p>
        <?php _e('Check out my other useful plugins', 'include-me') ?>:
        <a href="http://www.satollo.net/plugins/comment-plus?utm_source=include-me&utm_medium=link&utm_campaign=comment-plus" target="_blank">Comment Plus</a>,
        <a href="http://www.satollo.net/plugins/hyper-cache?utm_source=include-me&utm_medium=link&utm_campaign=hyper-cache" target="_blank">Hyper Cache</a>,
        <a href="http://www.thenewsletterplugin.com/?utm_source=include-me&utm_medium=link&utm_campaign=newsletter" target="_blank">Newsletter</a>,
        <a href="http://www.satollo.net/plugins/header-footer?utm_source=include-me&utm_medium=link&utm_campaign=header-footer" target="_blank">Header and footer</a>,
        <a href="http://www.satollo.net/plugins/thumbnails?utm_source=include-me&utm_medium=link&utm_campaign=thumbnails" target="_blank">Thumbnails</a>.
    </p>

    <h3><?php _e('Configuration', 'include-me') ?></h3>

    <form action="" method="post">
        <?php wp_nonce_field('save') ?>
        <table class="form-table">
            <tr>
                <th><?php _e('Execute shortcodes', 'include-me') ?></th>
                <td>
                    <input type="checkbox" name="options[shortcode]" value="1" <?php echo isset($options['shortcode']) ? 'checked' : ''; ?>>
                    <p class="description">
                        <?php _e('When checked short codes (like [gallery]) contained in included files will be executed as if they where inside the post or page body content. Probably usage of this feature is very rare.', 'include-me') ?>
                    </p>
                </td>
            </tr>    
        </table>
        <p class="submit">
            <input class="button button-primary" type="submit" name="save" value="<?php _e('Save') ?>"/>
        </p>


        <h3>Where is it used?</h3>

        <?php if (isset($posts)) { ?>
            <?php if (empty($posts)) { ?>
                <p>No posts or pages with [includeme] shortcode.</p>
            <?php } else { ?>
                <ul>
                    <?php foreach ($posts as $post) { ?>
                        <li><a href="<?php echo get_permalink($post->id) ?>" target="_blank"><?php echo esc_html($post->post_title) ?></a></li>
                    <?php } ?>
                </ul>
            <?php } ?>
        <?php } ?>

        <p class="submit">
            <input class="button button-primary" type="submit" name="find" value="<?php _e('Find') ?>"/>
        </p>
    </form>
</div>
