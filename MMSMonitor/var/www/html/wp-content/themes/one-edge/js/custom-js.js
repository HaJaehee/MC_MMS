jQuery( document ).ready( function($){

	$( '.timeline-box-wrap .info .entry-header' ).wrapInner( '<div class="entry-header-middle "></div>' );


} );

/* same size for all items in latest news section on homepage */
jQuery( document ).ready( home_latest_news_height );
jQuery( window ).resize( home_latest_news_height );
function home_latest_news_height($) {
	var winWidth = window.innerWidth;
	if( winWidth>768 ) {
		jQuery( '#timeline-scroll > li' ).each( function() {
			var thisHeightOne = jQuery( this ).find( '.timeline-box-wrap p:eq(0)' ).height();
			var thisHeightTwo = jQuery( this ).find( '.timeline-box-wrap p:eq(1)' ).height();
			jQuery( this ).find( '.timeline-box-wrap p' ).height( Math.max( thisHeightOne, thisHeightTwo ) );
		} );
	}
}