<?php

/*
Plugin Name: Huge IT Image Gallery
Plugin URI: http://huge-it.com/wordpress-gallery/
Description: Huge-IT Image Gallery is the best plugin to use if you want to be original with your website.
Version: 1.9.1
Author: Huge-IT
Author: http://huge-it.com/
License: GNU/GPLv3 http://www.gnu.org/licenses/gpl-3.0.html
*/

function gallery_images_load_plugin_textdomain() {
    load_plugin_textdomain( 'gallery-images', FALSE, plugin_basename( dirname( __FILE__ ) ) . '/languages' );
}

add_action( 'plugins_loaded', 'gallery_images_load_plugin_textdomain' );


add_action('media_buttons_context', 'add_gallery_my_custom_button');
add_action('admin_footer', 'add_gallery_inline_popup_content');
add_action( 'wp_ajax_huge_it_video_gallery_ajax', 'huge_it_image_gallery_ajax_callback' );
add_action( 'wp_ajax_nopriv_huge_it_video_gallery_ajax', 'huge_it_image_gallery_ajax_callback' );
global $huge_it_ip;
    $huge_it_ip='';

if(!empty($_SERVER['HTTP_CLIENT_IP'])){
  $huge_it_ip=$_SERVER['HTTP_CLIENT_IP'];
}
elseif(!empty($_SERVER['HTTP_X_FORWARDED_FOR'])){
  $huge_it_ip=$_SERVER['HTTP_X_FORWARDED_FOR'];
}
else{
  $huge_it_ip=$_SERVER['REMOTE_ADDR'];
}
function huge_it_image_gallery_ajax_callback(){
if(!function_exists('get_video_gallery_id_from_url')) {
    function get_video_gallery_id_from_url($url){
    if(strpos($url,'youtube') !== false || strpos($url,'youtu') !== false){ 
        if (preg_match('%(?:youtube(?:-nocookie)?\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\.be/)([^"&?/ ]{11})%i', $url, $match)) {
            return array ($match[1],'youtube');
        }
    }else {
        $vimeoid =  explode( "/", $url );
        $vimeoid =  end($vimeoid);
        return array($vimeoid,'vimeo');
    }
}
}
if(!function_exists('youtube_or_vimeo')) {
        function youtube_or_vimeo($videourl){
    if(strpos($videourl,'youtube') !== false || strpos($videourl,'youtu') !== false){   
        if (preg_match('%(?:youtube(?:-nocookie)?\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\.be/)([^"&?/ ]{11})%i', $videourl, $match)) {
            return 'youtube';
        }
    }
    elseif(strpos($videourl,'vimeo') !== false && strpos($videourl,'video') !== false) {
        $explode = explode("/",$videourl);
        $end = end($explode);
        if(strlen($end) == 8)
            return 'vimeo';
    }
    return 'image';
}
}
if(!function_exists('get_huge_image')) {
        function get_huge_image($image_url,$img_prefix) {
            //if(huge_it_copy_image_to_small($image_url,$image_prefix,$cropwidth)) {
                $pathinfo = pathinfo($image_url);
                $upload_dir = wp_upload_dir();
                $url_img_copy = $upload_dir["url"].'/'.$pathinfo["filename"].$img_prefix.'.'.$pathinfo["extension"];
                $img_abs_path = $url_img_copy;
                $img_abs_path= parse_url($url_img_copy, PHP_URL_PATH);
                $img_abs_path =  $_SERVER['DOCUMENT_ROOT'].$img_abs_path;
                if(file_exists($img_abs_path))
                return $url_img_copy; else
            //}
             return $image_url;
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////
if(isset($_POST['task']) && $_POST['task']=="load_images_content"){
    global $wpdb;
    global $huge_it_ip;
    $page = 1;
    if(!empty($_POST["page"]) && is_numeric($_POST['page']) && $_POST['page']>0){
        $page = $_POST["page"];
        $num=$_POST['perpage'];
        $start = $page * $num - $num; 
        $idofgallery=$_POST['galleryid'];
        $pID=$_POST['pID'];
        $likeStyle=$_POST['likeStyle'];
        $ratingCount=$_POST['ratingCount'];
         $query=$wpdb->prepare("SELECT * FROM ".$wpdb->prefix."huge_itgallery_images where gallery_id = '%d' order by ordering ASC LIMIT %d,%d",$idofgallery,$start,$num);
       $page_images=$wpdb->get_results($query);
        $output = '';
        foreach($page_images as $key=>$row)
    {
        if(!isset($_COOKIE['Like_'.$row->id.'']))$_COOKIE['Like_'.$row->id.'']='';
        if(!isset($_COOKIE['Dislike_'.$row->id.'']))$_COOKIE['Dislike_'.$row->id.'']='';
        $num2=$wpdb->prepare("SELECT `image_status`,`ip` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip` = '".esc_html($huge_it_ip)."'",(int)$row->id);
        $res3=$wpdb->get_row($num2);
        $num3=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_COOKIE['Like_'.$row->id.'']."'",(int)$row->id);
        $res4=$wpdb->get_row($num3);
        $num4=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_COOKIE['Dislike_'.$row->id.'']."'",(int)$row->id);
        $res5=$wpdb->get_row($num4);
        $link = $row->sl_url;
        $video_name=
str_replace('__5_5_5__','%',$row->name);
        $id=$row->id;
        $descnohtml=strip_tags(
str_replace('__5_5_5__','%',$row->description));
        $result = substr($descnohtml, 0, 50);
        ?>
                <?php 
                    $imagerowstype=$row->sl_type;
                    if($row->sl_type == ''){$imagerowstype='image';}
                    switch($imagerowstype){
                        case 'image':
                ?>                                  
                            <?php $imgurl=explode(";",$row->image_url); ?>
                           <?php    if($row->image_url != ';'){ 
                            $video='<img id="wd-cl-img'.$key.'" src="'.$imgurl[0].'" alt="" />';
                             } else {
                            $video='<img id="wd-cl-img'.$key.'" src="images/noimage.jpg" alt="" />';
                            } ?>
                <?php
                        break;
                        case 'video':
                ?>
                        <?php
                            $videourl=get_video_gallery_id_from_url($row->image_url);
                            if($videourl[1]=='youtube'){
                                    if(empty($row->thumb_url)){
                                            $thumb_pic='http://img.youtube.com/vi/'.$videourl[0].'/mqdefault.jpg';
                                        }else{
                                            $thumb_pic=$row->thumb_url;
                                        }
                                $video='<img src="'.$thumb_pic.'" alt="" />';                             
                                }else {
                                $hash = unserialize(file_get_contents("http://vimeo.com/api/v2/video/".$videourl[0].".php"));
                                if(empty($row->thumb_url)){
                                        $imgsrc=$hash[0]['thumbnail_large'];
                                    }else{
                                        $imgsrc=$row->thumb_url;
                                    }
                                $video='<img src="'.$imgsrc.'" alt="" />';
                            }
                        ?>
                <?php
                        break;
                    }
                ?>
           <?php if($row->sl_url==''){
                $button='';
            }else{
                if ($row->link_target=="on"){
                    $target='target="_blank"';
                }else{
                    $target='';
                }
                $button='<div class="button-block"><a href="'.$row->sl_url.'" '.$target.' >'. esc_html($_POST['linkbutton']).'</a></div>';
            }
            ?>
    <?php
            $thumb_status_like='';
            if(isset($res3->image_status)&&$res3->image_status=='liked'){
                $thumb_status_like=$res3->image_status;
            }elseif (isset($res4->image_status)&&$res4->image_status=='liked') {
                $thumb_status_like=$res4->image_status;
            }else{
                $thumb_status_like='unliked'; 
            }
            $thumb_status_dislike='';
            if(isset($res3->image_status)&&$res3->image_status=='disliked'){
                $thumb_status_dislike=$res3->image_status;
            }elseif (isset($res5->image_status)&&$res5->image_status=='disliked') {
                $thumb_status_dislike=$res5->image_status;
            }else{
                $thumb_status_dislike='unliked'; 
            }
            $likeIcon='';
            if($likeStyle == 'heart'){
                    $likeIcon='<i class="hugeiticons-heart likeheart"></i>'; 
            }elseif($likeStyle == 'dislike'){
                $likeIcon='<i class="hugeiticons-thumbs-up like_thumb_up"></i>';
            }
            $likeCount='';
            if($likeStyle != 'heart'){
                $likeCount=$row->like;
            }
            $thumb_text_like='';
            if($likeStyle == 'heart'){
                    $thumb_text_like=$row->like;
            }
            $displayCount='';
            if($ratingCount =='off'){
                $displayCount='huge_it_hide';
            }
            $dislikeHtml='';  
            if($likeStyle != 'heart'){                                             
             $dislikeHtml='<div class="huge_it_gallery_dislike_wrapper">
                                <span class="huge_it_dislike">
                                    <i class="hugeiticons-thumbs-down dislike_thumb_down"></i>
                                    <span class="huge_it_dislike_thumb" id="'.$row->id.'" data-status="'.$thumb_status_dislike.'"></span>
                                    <span class="huge_it_dislike_count '.$displayCount.'" id="'.$row->id.'">'.$row->dislike.'</span>
                                </span>
                            </div>';             
            }
/////////////////////////////
            if($likeStyle != 'off'){
                $likeCont='<div class="huge_it_gallery_like_cont_'.$idofgallery.$pID.'">
                                <div class="huge_it_gallery_like_wrapper">
                                    <span class="huge_it_like">'.$likeIcon.'
                                        <span class="huge_it_like_thumb" id="'.$row->id.'" data-status="'.$thumb_status_like.'">'.$thumb_text_like.'</span>
                                        <span class="huge_it_like_count '.$displayCount.'" id="'.$row->id.'">'.$likeCount.'</span>
                                    </span>
                                </div>'.$dislikeHtml.'
                           </div>';
           }
///////////////////////////////
            $output.='<div class="element_'.$idofgallery.' " tabindex="0" data-symbol="'.$video_name.'"  data-category="alkaline-earth">';
            $output.='<input type="hidden" class="pagenum" value="'.$page.'" />';
            $output.='<div class="image-block_'.$idofgallery.'">';
            $output.=$video;
            $output.='<div class="gallery-image-overlay"><a href="#'.$id.'"></a>'.$likeCont.'
                         </div>';
            $output.='</div>';
            $output.='<div class="title-block_'.$idofgallery.'">';
            $output.='<h3>'.$video_name.'</h3>';
            $output.=$button;
            $output.='</div>';
            $output.='</div>';
     }
        echo json_encode(array("success"=>$output));
        die();
    }
}
///////////////////////////////////////////////////////////////////////////////////////////////
if(isset($_POST['task']) && $_POST['task']=="load_images_lightbox"){
        global $wpdb;
        global $huge_it_ip;
    $page = 1;
    if(!empty($_POST["page"]) && is_numeric($_POST['page']) && $_POST['page']>0){
        $page = $_POST["page"];
        $num=$_POST['perpage'];
        $start = $page * $num - $num; 
        $idofgallery=$_POST['galleryid'];
        $pID=$_POST['pID'];
        $likeStyle=$_POST['likeStyle'];
        $ratingCount=$_POST['ratingCount'];
         $query=$wpdb->prepare("SELECT * FROM ".$wpdb->prefix."huge_itgallery_images where gallery_id = '%d' order by ordering ASC LIMIT %d,%d",$idofgallery,$start,$num);
       $page_images=$wpdb->get_results($query);
        $output = '';
        foreach($page_images as $key=>$row)
    {
        if(!isset($_COOKIE['Like_'.$row->id.'']))$_COOKIE['Like_'.$row->id.'']='';
        if(!isset($_COOKIE['Dislike_'.$row->id.'']))$_COOKIE['Dislike_'.$row->id.'']='';
        $num2=$wpdb->prepare("SELECT `image_status`,`ip` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip` = '".esc_html($huge_it_ip)."'",(int)$row->id);
        $res3=$wpdb->get_row($num2);
        $num3=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_COOKIE['Like_'.$row->id.'']."'",(int)$row->id);
        $res4=$wpdb->get_row($num3);
        $num4=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_COOKIE['Dislike_'.$row->id.'']."'",(int)$row->id);
        $res5=$wpdb->get_row($num4);
        $link = $row->sl_url;
        $video_name=
str_replace('__5_5_5__','%',$row->name);
        $descnohtml=strip_tags(str_replace('__5_5_5__','%',$row->description));
        $result = substr($descnohtml, 0, 50);
        ?>
                <?php 
                    $imagerowstype=$row->sl_type;
                    if($row->sl_type == ''){$imagerowstype='image';}
                    switch($imagerowstype){
                        case 'image':
                ?>                                  
                            <?php $imgurl=explode(";",$row->image_url); ?>
                            <?php  
                             if($row->image_url != ';'){ 
                            $video='<a href="'.$imgurl[0].'" title="'.$video_name.'"><img id="wd-cl-img'.$key.'" src="'.$imgurl[0].'" alt="'.$video_name.'" /></a>';
                            } 
                            else { 
                            $video='<img id="wd-cl-img'.$key.'" src="images/noimage.jpg" alt="" />';
                            } ?>
                <?php
                        break;
                        case 'video':
                ?>
                        <?php
                            $videourl=get_video_gallery_id_from_url($row->image_url);
                            if($videourl[1]=='youtube'){
                                    if(empty($row->thumb_url)){
                                            $thumb_pic='http://img.youtube.com/vi/'.$videourl[0].'/mqdefault.jpg';
                                        }else{
                                            $thumb_pic=$row->thumb_url;
                                        }
                                $video='<a class="youtube huge_it_videogallery_item group1"  href="https://www.youtube.com/embed/'.$videourl[0].'" title="'.$video_name.'">
                                            <img src="'.$thumb_pic.'" alt="'.$video_name.'" />
                                            <div class="play-icon '.$videourl[1].'-icon"></div>
                                        </a>';                             
                                }else {
                                $hash = unserialize(file_get_contents("http://vimeo.com/api/v2/video/".$videourl[0].".php"));
                                if(empty($row->thumb_url)){
                                        $imgsrc=$hash[0]['thumbnail_large'];
                                    }else{
                                        $imgsrc=$row->thumb_url;
                                    }
                                $video='<a class="vimeo huge_it_videogallery_item group1" href="http://player.vimeo.com/video/'.$videourl[0].'" title="'.$video_name.'">
                                    <img src="'.$imgsrc.'" alt="" />
                                    <div class="play-icon '.$videourl[1].'-icon"></div>
                                </a>';
                            }
                        ?>
                <?php
                        break;
                    }
                ?>
         <?php if(
str_replace('__5_5_5__','%',$row->name)!=""){
                if ($row->link_target=="on"){
                   $target= 'target="_blank"';
                }else{
                    $target= '';
                }
               $linkimg='<div class="title-block_'.$idofgallery.'"><a href="'.$link.'"'.$target.'>'.$video_name.'</a></div>';
            }else{
                $linkimg='';
            } 
            ?>
    <?php
            $thumb_status_like='';
            if(isset($res3->image_status)&&$res3->image_status=='liked'){
                $thumb_status_like=$res3->image_status;
            }elseif (isset($res4->image_status)&&$res4->image_status=='liked') {
                $thumb_status_like=$res4->image_status;
            }else{
                $thumb_status_like='unliked'; 
            }
            $thumb_status_dislike='';
            if(isset($res3->image_status)&&$res3->image_status=='disliked'){
                $thumb_status_dislike=$res3->image_status;
            }elseif (isset($res5->image_status)&&$res5->image_status=='disliked') {
                $thumb_status_dislike=$res5->image_status;
            }else{
                $thumb_status_dislike='unliked'; 
            }
            $likeIcon='';
            if($likeStyle == 'heart'){
                    $likeIcon='<i class="hugeiticons-heart likeheart"></i>'; 
            }elseif($likeStyle == 'dislike'){
                $likeIcon='<i class="hugeiticons-thumbs-up like_thumb_up"></i>';
            }
            $likeCount='';
            if($likeStyle != 'heart'){
                $likeCount=$row->like;
            } 
            $thumb_text_like='';
            if($likeStyle == 'heart'){
                    $thumb_text_like=$row->like;
            } 
            $displayCount='';
            if($ratingCount =='off'){
                $displayCount='huge_it_hide';
            }
            $dislikeHtml='';
            if($likeStyle != 'heart'){                                             
             $dislikeHtml='<div class="huge_it_gallery_dislike_wrapper">
                                <span class="huge_it_dislike">
                                    <i class="hugeiticons-thumbs-down dislike_thumb_down"></i>
                                    <span class="huge_it_dislike_thumb" id="'.$row->id.'" data-status="'.$thumb_status_dislike.'">
                                    </span>
                                    <span class="huge_it_dislike_count '.$displayCount.'" id="'.$row->id.'">'.$row->dislike.'</span>
                                </span>
                            </div>';             
            }
/////////////////////////////
            if($likeStyle != 'off'){
                $likeCont='<div class="huge_it_gallery_like_cont_'.$idofgallery.$pID.'">
                                <div class="huge_it_gallery_like_wrapper">
                                    <span class="huge_it_like">'.$likeIcon.'
                                        <span class="huge_it_like_thumb" id="'.$row->id.'" data-status="'.$thumb_status_like.'">'.$thumb_text_like.'</span>
                                        <span class="huge_it_like_count '.$displayCount.'" id="'.$row->id.'">'.$likeCount.'</span>
                                    </span>
                                </div>'.$dislikeHtml.'
                           </div>';
           }
///////////////////////////////
            $output.='<div class="element_'.$idofgallery.'" tabindex="0" data-symbol="'.$video_name.'"  data-category="alkaline-earth">';
            $output.='<input type="hidden" class="pagenum" value="'.$page.'" />';
            $output.='<div class="image-block_'.$idofgallery.'">';
            $output.=$video;
            $output.=$linkimg;
            $output.=$likeCont;
            $output.='</div>';
            $output.='</div>';
       }
        echo json_encode(array("success"=>$output));
        die();
    }
}
////////////////////////////////////////////////////////////////////////////////////////////
if(isset($_POST['task']) && $_POST['task']=="load_image_justified"){
        global $wpdb;
        global $huge_it_ip;
    $page = 1;
    if(!empty($_POST["page"]) && is_numeric($_POST['page']) && $_POST['page']>0){
        $page = $_POST["page"];
        $num=$_POST['perpage'];
        $start = $page * $num - $num; 
        $idofgallery=$_POST['galleryid'];
        $pID=$_POST['pID'];
        $likeStyle=$_POST['likeStyle'];
        $ratingCount=$_POST['ratingCount'];
         $query=$wpdb->prepare("SELECT * FROM ".$wpdb->prefix."huge_itgallery_images where gallery_id = '%d' order by ordering ASC LIMIT %d,%d",$idofgallery,$start,$num);
        $output = '';
        $page_images=$wpdb->get_results($query);
        foreach($page_images as $key=>$row){
            if(!isset($_COOKIE['Like_'.$row->id.'']))$_COOKIE['Like_'.$row->id.'']='';
            if(!isset($_COOKIE['Dislike_'.$row->id.'']))$_COOKIE['Dislike_'.$row->id.'']='';
            $num2=$wpdb->prepare("SELECT `image_status`,`ip` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip` = '".esc_html($huge_it_ip)."'",(int)$row->id);
            $res3=$wpdb->get_row($num2);
            $num3=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_COOKIE['Like_'.$row->id.'']."'",(int)$row->id);
            $res4=$wpdb->get_row($num3);
            $num4=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_COOKIE['Dislike_'.$row->id.'']."'",(int)$row->id);
            $res5=$wpdb->get_row($num4);
            $video_name=str_replace('__5_5_5__','%',$row->name);
         $videourl=get_video_gallery_id_from_url($row->image_url);
         $imgurl=explode(";",$row->image_url);
         $image_prefix = "_huge_it_small_gallery";
         $imagerowstype=$row->sl_type; 
            $thumb_status_like='';
            if(isset($res3->image_status)&&$res3->image_status=='liked'){
                $thumb_status_like=$res3->image_status;
            }elseif (isset($res4->image_status)&&$res4->image_status=='liked') {
                $thumb_status_like=$res4->image_status;
            }else{
                $thumb_status_like='unliked'; 
            }
            $thumb_status_dislike='';
            if(isset($res3->image_status)&&$res3->image_status=='disliked'){
                $thumb_status_dislike=$res3->image_status;
            }elseif (isset($res5->image_status)&&$res5->image_status=='disliked') {
                $thumb_status_dislike=$res5->image_status;
            }else{
                $thumb_status_dislike='unliked'; 
            }
            $likeIcon='';
            if($likeStyle == 'heart'){
                    $likeIcon='<i class="hugeiticons-heart likeheart"></i>'; 
            }elseif($likeStyle == 'dislike'){
                $likeIcon='<i class="hugeiticons-thumbs-up like_thumb_up"></i>';
            }
            $likeCount='';
            if($likeStyle != 'heart'){
                $likeCount=$row->like;
            } 
            $thumb_text_like='';
            if($likeStyle == 'heart'){
                    $thumb_text_like=$row->like;
            }  
            $displayCount='';
            if($ratingCount =='off'){
                $displayCount='huge_it_hide';
            }
            $dislikeHtml='';
            if($likeStyle != 'heart'){                                             
             $dislikeHtml='<div class="huge_it_gallery_dislike_wrapper">
                                <span class="huge_it_dislike">
                                    <i class="hugeiticons-thumbs-down dislike_thumb_down"></i>
                                    <span class="huge_it_dislike_thumb" id="'.$row->id.'" data-status="'.$thumb_status_dislike.'">
                                    </span>
                                    <span class="huge_it_dislike_count '.$displayCount.'" id="'.$row->id.'">'.$row->dislike.'</span>
                                </span>
                            </div>';             
            }
/////////////////////////////
            if($likeStyle != 'off'){
                $likeCont='<div class="huge_it_gallery_like_cont_'.$idofgallery.$pID.'">
                                <div class="huge_it_gallery_like_wrapper">
                                    <span class="huge_it_like">'.$likeIcon.'
                                        <span class="huge_it_like_thumb" id="'.$row->id.'" data-status="'.$thumb_status_like.'">'.$thumb_text_like.'
                                        </span>
                                        <span class="huge_it_like_count '.$displayCount.'" id="'.$row->id.'">'.$likeCount.'</span>
                                    </span>
                                </div>'.$dislikeHtml.'
                           </div>';
           }
///////////////////////////////
                    if($row->sl_type == ''){$imagerowstype='image';}
                    switch($imagerowstype){
                        case 'image': 
                                 if($row->image_url != ';'){ 
                                    $imgperfix=get_huge_image($imgurl[0],$image_prefix);
                                       $video= '<a class="gallery_group'.$idofgallery.'" href="'.$imgurl[0].'" title="'.$video_name.'">
                                            <img  id="wd-cl-img'.$key.'" alt="'.$video_name.'" src="'.$imgperfix.'"/>
                                            '.$likeCont.'
                                        </a>
                                        <input type="hidden" class="pagenum" value="'.$page.'" />';?>
                                <?php } else { 
                                       $video= '<img alt="'.$video_name.'" id="wd-cl-img'.$key.'" src="images/noimage.jpg"  />
                                                '.$likeCont.'
                                        <input type="hidden" class="pagenum" value="'.$page.'" />';
                                } ?>
                    <?php 
                        break;
                        case 'video':
            if($videourl[1]=='youtube'){
                if(empty($row->thumb_url)){
                                            $thumb_pic='http://img.youtube.com/vi/'.$videourl[0].'/mqdefault.jpg';
                                        }else{
                                            $thumb_pic=$row->thumb_url;
                                        }
                $video = '<a class="youtube huge_it_videogallery_item gallery_group'.$idofgallery.'"  href="https://www.youtube.com/embed/'.$videourl[0].'" title="'.$video_name.'">
                                                <img  src="'.$thumb_pic.'" alt="'.$video_name.'" />
                                                '.$likeCont.'
                                                <div class="play-icon '.$videourl[1].'-icon"></div>
                                        </a>';
            }else {
                $hash = unserialize(file_get_contents("http://vimeo.com/api/v2/video/".$videourl[0].".php"));
                                    if(empty($row->thumb_url)){
                                        $imgsrc=$hash[0]['thumbnail_large'];
                                    }else{
                                        $imgsrc=$row->thumb_url;
                                    }
                $video = '<a class="vimeo huge_it_videogallery_item gallery_group'.$idofgallery.'" href="http://player.vimeo.com/video/'.$videourl[0].'" title="'.$video_name.'">
                                                <img alt="'.$video_name.'" src="'.$imgsrc.'"/>
                                                '.$likeCont.'
                                                <div class="play-icon '.$videourl[1].'-icon"></div>
                                        </a>';
            }
                break;
            }
            $output .=$video.'<input type="hidden" class="pagenum" value="'.$page.'" />';
        }
        echo json_encode(array("success"=>$output));
        die();
    }
}
////////////////////////////////////////////////////////////////////////////////////////////
if(isset($_POST['task']) && $_POST['task']=="load_image_thumbnail"){
        global $wpdb;
        global $huge_it_ip;
    $page = 1;
    if(!empty($_POST["page"]) && is_numeric($_POST['page']) && $_POST['page']>0){
        $page = $_POST["page"];
        $num=$_POST['perpage'];
        $start = $page * $num - $num; 
        $idofgallery=$_POST['galleryid'];
        $pID=$_POST['pID'];
        $likeStyle=$_POST['likeStyle'];
        $ratingCount=$_POST['ratingCount'];
         $query=$wpdb->prepare("SELECT * FROM ".$wpdb->prefix."huge_itgallery_images where gallery_id = '%d' order by ordering ASC LIMIT %d,%d",$idofgallery,$start,$num);
        $output = '';
        $page_images=$wpdb->get_results($query);
        foreach($page_images as $key=>$row){
            //var_dump($icon);
            if(!isset($_COOKIE['Like_'.$row->id.'']))$_COOKIE['Like_'.$row->id.'']='';
            if(!isset($_COOKIE['Dislike_'.$row->id.'']))$_COOKIE['Dislike_'.$row->id.'']='';
            $num2=$wpdb->prepare("SELECT `image_status`,`ip` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip` = '".esc_html($huge_it_ip)."'",(int)$row->id);
            $res3=$wpdb->get_row($num2);
            $num3=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_COOKIE['Like_'.$row->id.'']."'",(int)$row->id);
            $res4=$wpdb->get_row($num3);
            $num4=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_COOKIE['Dislike_'.$row->id.'']."'",(int)$row->id);
            $res5=$wpdb->get_row($num4);
            $video_name=str_replace('__5_5_5__','%',$row->name);
            $imgurl=explode(";",$row->image_url); 
            $image_prefix = "_huge_it_small_gallery";
            $videourl=get_video_gallery_id_from_url($row->image_url);
         $imagerowstype=$row->sl_type; 
                    if($row->sl_type == ''){$imagerowstype='image';}
                    switch($imagerowstype){
                        case 'image': 
                        $imgperfix=get_huge_image($imgurl[0],$image_prefix);
                         $video='<a class="gallery_group'.$idofgallery.'" href="'.$row->image_url.'" title="'.$video_name.'"></a>
                            <img  src="'.$imgperfix.'" alt="'.$video_name.'" />';
                        break;
                        case 'video':
                                if($videourl[1]=='youtube'){
                                    $video='<a class="youtube huge_it_gallery_item gallery_group'.$idofgallery.'"  href="https://www.youtube.com/embed/'.$videourl[0].'" title="'.str_replace("__5_5_5__","%",$row->name).'"></a>
                                    <img alt="'.str_replace("__5_5_5__","%",$row->name).'" src="http://img.youtube.com/vi/'.$videourl[0].'/mqdefault.jpg"  />';              
                                }else {
                                    $hash = unserialize(file_get_contents("http://vimeo.com/api/v2/video/".$videourl[0].".php"));
                                    $imgsrc=$hash[0]['thumbnail_large'];
                                    $video='<a class="vimeo huge_it_gallery_item gallery_group'.$idofgallery.'" href="http://player.vimeo.com/video/'.$videourl[0].'" title="'.str_replace("__5_5_5__","%",$row->name).'"></a>
                                    <img alt="'.str_replace("__5_5_5__","%",$row->name).'" src="'.$imgsrc.'"  />';
                                }
                            ?>
                    <?php
                        break;
                    }
                    ?>
<?php
            $thumb_status_like='';
            if(isset($res3->image_status)&&$res3->image_status=='liked'){
                $thumb_status_like=$res3->image_status;
            }elseif (isset($res4->image_status)&&$res4->image_status=='liked') {
                $thumb_status_like=$res4->image_status;
            }else{
                $thumb_status_like='unliked'; 
            }
            $thumb_status_dislike='';
            if(isset($res3->image_status)&&$res3->image_status=='disliked'){
                $thumb_status_dislike=$res3->image_status;
            }elseif (isset($res5->image_status)&&$res5->image_status=='disliked') {
                $thumb_status_dislike=$res5->image_status;
            }else{
                $thumb_status_dislike='unliked'; 
            }
            $likeIcon='';
            if($likeStyle == 'heart'){
                    $likeIcon='<i class="hugeiticons-heart likeheart"></i>'; 
            }elseif($likeStyle == 'dislike'){
                $likeIcon='<i class="hugeiticons-thumbs-up like_thumb_up"></i>';
            }
            $likeCount='';
            if($likeStyle != 'heart'){
                $likeCount=$row->like;
            }  
            $thumb_text_like='';
            if($likeStyle == 'heart'){
                    $thumb_text_like=$row->like;
            } 
            $displayCount='';
            if($ratingCount =='off'){
                $displayCount='huge_it_hide';
            }
            $dislikeHtml='';
            if($likeStyle != 'heart'){                                             
             $dislikeHtml='<div class="huge_it_gallery_dislike_wrapper">
                                <span class="huge_it_dislike">
                                    <i class="hugeiticons-thumbs-down dislike_thumb_down"></i>
                                    <span class="huge_it_dislike_thumb" id="'.$row->id.'" data-status="'.$thumb_status_dislike.'">
                                    </span>
                                    <span class="huge_it_dislike_count '.$displayCount.'" id="'.$row->id.'">'.$row->dislike.'</span>
                                </span>
                            </div>';             
            }
/////////////////////////////
            if($likeStyle != 'off'){
                $likeCont='<div class="huge_it_gallery_like_cont_'.$idofgallery.$pID.'">
                                <div class="huge_it_gallery_like_wrapper">
                                    <span class="huge_it_like">'.$likeIcon.'
                                        <span class="huge_it_like_thumb" id="'.$row->id.'" data-status="'.$thumb_status_like.'">'.$thumb_text_like.'
                                        </span>
                                        <span class="huge_it_like_count '.$displayCount.'" id="'.$row->id.'">'.$likeCount.'</span>
                                    </span>
                                </div>'.$dislikeHtml.'
                           </div>';
           }
///////////////////////////////
            $output .='
                <li class="huge_it_big_li">
                     '.$likeCont.'<input type="hidden" class="pagenum" value="'.$page.'" />
                        '.$video.'
                    <div class="overLayer"></div>
                    <div class="infoLayer">
                        <ul>
                            <li>
                                <h2>
                                    '.$video_name.'
                                </h2>
                            </li>
                            <li>
                                <p>
                                    '. esc_html($_POST['thumbtext']).'
                                </p>
                            </li>
                        </ul>
                    </div>
                </li>
            ';
        }
        echo json_encode(array("success"=>$output));
        die();
    }
}
///////////////////////////////////////////////////////////////////////////////////////////
   if(isset($_POST['task']) && $_POST['task']=="load_blog_view"){
        global $wpdb;
        global $huge_it_ip;
    $page = 1;
    if(!empty($_POST["page"]) && is_numeric($_POST['page']) && $_POST['page']>0){
        $page = $_POST["page"];
        $num=$_POST['perpage'];
        $start = $page * $num - $num; 
        $idofgallery=$_POST['galleryid'];
        $pID=$_POST['pID'];
        $likeStyle=$_POST['likeStyle'];
        $ratingCount=$_POST['ratingCount'];
         $query=$wpdb->prepare("SELECT * FROM ".$wpdb->prefix."huge_itgallery_images where gallery_id = '%d' order by ordering ASC LIMIT %d,%d",$idofgallery,$start,$num);
        $output = '';
        $page_images=$wpdb->get_results($query);
       foreach($page_images as $key=>$row)
    {
        $img2video='';
        if(!isset($_COOKIE['Like_'.$row->id.'']))$_COOKIE['Like_'.$row->id.'']='';
        if(!isset($_COOKIE['Dislike_'.$row->id.'']))$_COOKIE['Dislike_'.$row->id.'']='';
        $num2=$wpdb->prepare("SELECT `image_status`,`ip` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip` = '".esc_html($huge_it_ip)."'",(int)$row->id);
        $res3=$wpdb->get_row($num2);
        $num3=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_COOKIE['Like_'.$row->id.'']."'",(int)$row->id);
        $res4=$wpdb->get_row($num3);
        $num4=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_COOKIE['Dislike_'.$row->id.'']."'",(int)$row->id);
        $res5=$wpdb->get_row($num4);
        $img_src=$row->image_url;
        $img_name=str_replace('__5_5_5__','%',$row->name);
        $img_desc=str_replace('__5_5_5__','%',$row->description);
        $videourl=get_video_gallery_id_from_url($row->image_url);
        $imagerowstype=$row->sl_type;
        if($imagerowstype == ''){$imagerowstype='image';}
            if($imagerowstype=='image'){          
                        $img2video .='<img class="view9_img" src="'.$img_src.'">';
                  }
                else{
                         if($videourl[1]=='youtube'){
                        $img3video .='<div class="iframe_cont">
                                        <iframe class="video_blog_view" src="//www.youtube.com/embed/'.$videourl[0].'" style="border: 0;" allowfullscreen></iframe>
                                    </div>';
                        }else{
                                $img3video .='<div class="iframe_cont">
                                                <iframe class="video_blog_view" src="//player.vimeo.com/video/'.$videourl[0].'" style="border: 0;" allowfullscreen></iframe>
                                            </div>';
                        } 
                }
                   if($imagerowstype=='image'){
                        $link_img_video=$img2video;
                   }else{
                        $link_img_video=$img3video;
                   }    
            $thumb_status_like='';
            if(isset($res3->image_status)&&$res3->image_status=='liked'){
                $thumb_status_like=$res3->image_status;
            }elseif (isset($res4->image_status)&&$res4->image_status=='liked') {
                $thumb_status_like=$res4->image_status;
            }else{
                $thumb_status_like='unliked'; 
            }
            $thumb_status_dislike='';
            if(isset($res3->image_status)&&$res3->image_status=='disliked'){
                $thumb_status_dislike=$res3->image_status;
            }elseif (isset($res5->image_status)&&$res5->image_status=='disliked') {
                $thumb_status_dislike=$res5->image_status;
            }else{
                $thumb_status_dislike='unliked'; 
            }
            $likeIcon='';
            if($likeStyle == 'heart'){
                    $likeIcon='<i class="hugeiticons-heart likeheart"></i>'; 
            }elseif($likeStyle == 'dislike'){
                $likeIcon='<i class="hugeiticons-thumbs-up like_thumb_up"></i>';
            }
            $likeCount='';
            if($likeStyle != 'heart'){
                $likeCount=$row->like;
            }
            $thumb_text_like='';
            if($likeStyle == 'heart'){
                    $thumb_text_like=$row->like;
            } 
             $displayCount='';
            if($ratingCount =='off'){
                $displayCount='huge_it_hide';
            }
            $dislikeHtml='';  
            if($likeStyle != 'heart'){                                             
             $dislikeHtml='<div class="huge_it_gallery_dislike_wrapper">
                                <span class="huge_it_dislike">
                                    <i class="hugeiticons-thumbs-down dislike_thumb_down"></i>
                                    <span class="huge_it_dislike_thumb" id="'.$row->id.'" data-status="'.$thumb_status_dislike.'">
                                    </span>
                                    <span class="huge_it_dislike_count '.$displayCount.'" id="'.$row->id.'">'.$row->dislike.'</span>
                                </span>
                            </div>';             
            }
/////////////////////////////
            if($likeStyle != 'off'){
                $likeCont='<div class="huge_it_gallery_like_cont_'.$idofgallery.$pID.'">
                                <div class="huge_it_gallery_like_wrapper">
                                    <span class="huge_it_like">'.$likeIcon.'
                                        <span class="huge_it_like_thumb" id="'.$row->id.'" data-status="'.$thumb_status_like.'">'.$thumb_text_like.'
                                        </span>
                                        <span class="huge_it_like_count '.$displayCount.'" id="'.$row->id.'">'.$likeCount.'</span>
                                    </span>
                                </div>'.$dislikeHtml.'
                           </div>';
           }
///////////////////////////////
           if($likeStyle != 'heart'){
               $output .='<div class="view9_container">
                                <input type="hidden" class="pagenum" value="'.$page.'" />
                                <h1 class="new_view_title">'.$img_name.'</h1>'.$link_img_video.'
                                <div class="new_view_desc">'.$img_desc.'</div>'.$likeCont.'
                          <div class="clear"></div>';
                      }
          if($likeStyle == 'heart'){
                $output .='<div class="view9_container">
                                <input type="hidden" class="pagenum" value="'.$page.'" />
                                <h1 class="new_view_title">'.$img_name.'</h1><div class="blog_img_wrapper">'.$link_img_video.$likeCont.'</div>
                                <div class="new_view_desc">'.$img_desc.'</div>
                          <div class="clear"></div>';
          }
                }
            }
        echo json_encode(array("success"=>$output,"typeOfres"=>$imagerowstype));
        die();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    if(isset($_POST['task']) && $_POST['task']=="like"){
        $huge_it_ip='';
    if(!empty($_SERVER['HTTP_CLIENT_IP'])){
      $huge_it_ip=$_SERVER['HTTP_CLIENT_IP'];
    }
    elseif(!empty($_SERVER['HTTP_X_FORWARDED_FOR'])){
      $huge_it_ip=$_SERVER['HTTP_X_FORWARDED_FOR'];
    }
    else{
      $huge_it_ip=$_SERVER['REMOTE_ADDR'];
    }
        global $wpdb;
        $num=$wpdb->prepare("SELECT `image_status`,`ip` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d",(int)$_POST['image_id']);
        $num2=$wpdb->prepare("SELECT `image_status`,`ip` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip` = '".$huge_it_ip."'",(int)$_POST['image_id']);  
        $res=$wpdb->get_results($num); 
        $res2=$wpdb->get_results($num,ARRAY_A);
        $res3=$wpdb->get_row($num2);
        $num3=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_POST['cook']."'",(int)$_POST['image_id']);
        $res4=$wpdb->get_row($num3);
            $resIP='';
            for ($i=0; $i <count($res2) ; $i++) {                 
                $resIP.=$res2[$i]['ip'].'|';                           
            }
            $arrIP = explode("|", $resIP);
                //if(!isset($res3->image_status))$res3->image_status='';
                if(!isset($res3) && !isset($res4)){
                    $wpdb->query($wpdb->prepare("INSERT INTO ".$wpdb->prefix."huge_itgallery_like_dislike (`image_id`,`image_status`,`ip`,`cook`) VALUES ( %d, 'liked', '".$huge_it_ip."',%s)",(int)$_POST['image_id'],$_POST['cook']));
                    $wpdb->query($wpdb->prepare("UPDATE ".$wpdb->prefix."huge_itgallery_images SET  `like` = `like`+1 WHERE id = %d ",(int)$_POST['image_id']));
                    $numLike=$wpdb->prepare("SELECT `like` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
                    $resLike=$wpdb->get_results($numLike); 
                    $numDislike=$wpdb->prepare("SELECT `dislike` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
                    $resDislike=$wpdb->get_results($numDislike);  
                    echo json_encode(array("like"=>$resLike[0]->like,"statLike"=>'Liked'));  
                }elseif((isset($res3)&&$res3->image_status=='liked'&&$res3->ip==$huge_it_ip)||(isset($res4)&&$res4->image_status=='liked'&&$res4->cook==$_POST['cook'])){
                    if(isset($res3)&&$res3->image_status=='liked'&&$res3->ip==$huge_it_ip){
                       $wpdb->query($wpdb->prepare("DELETE FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip`='".$huge_it_ip."'",(int)$_POST['image_id'])); 
                   }elseif(isset($res4)&&$res4->cook==$_POST['cook']){
                       $wpdb->query($wpdb->prepare("DELETE FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook`='".$_POST['cook']."'",(int)$_POST['image_id'])); 
                   }                   
                    $wpdb->query($wpdb->prepare("UPDATE ".$wpdb->prefix."huge_itgallery_images SET  `like` = `like`-1 WHERE id = %d ",(int)$_POST['image_id']));
                    $numLike=$wpdb->prepare("SELECT `like` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
                    $resLike=$wpdb->get_results($numLike); 
                    $numDislike=$wpdb->prepare("SELECT `dislike` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
                    $resDislike=$wpdb->get_results($numDislike);   
                    echo json_encode(array("like"=>$resLike[0]->like,"statLike"=>'Like'));
                }elseif((isset($res3)&&$res3->image_status=='disliked'&&$res3->ip==$huge_it_ip)||(isset($res4)&&$res4->image_status=='disliked'&&$res4->cook==$_POST['cook'])){
                    if(isset($res3)&&$res3->image_status=='disliked'&&$res3->ip==$huge_it_ip){
                        $wpdb->query($wpdb->prepare("DELETE FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip`='".$huge_it_ip."'",(int)$_POST['image_id']));
                    }elseif(isset($res4)&&$res4->image_status=='disliked'&&$res4->cook==$_POST['cook']){
                       $wpdb->query($wpdb->prepare("DELETE FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook`='".$_POST['cook']."'",(int)$_POST['image_id'])); 
                    }
                    $wpdb->query($wpdb->prepare("INSERT INTO ".$wpdb->prefix."huge_itgallery_like_dislike (`image_id`,`image_status`,`ip`,`cook`) VALUES ( %d, 'liked', '".$huge_it_ip."',%s)",(int)$_POST['image_id'],$_POST['cook']));
                    $wpdb->query($wpdb->prepare("UPDATE ".$wpdb->prefix."huge_itgallery_images SET  `like` = `like`+1 WHERE id = %d ",(int)$_POST['image_id']));
                    $wpdb->query($wpdb->prepare("UPDATE ".$wpdb->prefix."huge_itgallery_images SET  `dislike` = `dislike`-1 WHERE id = %d ",(int)$_POST['image_id']));
                    $numLike=$wpdb->prepare("SELECT `like` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
                    $resLike=$wpdb->get_results($numLike); 
                    $numDislike=$wpdb->prepare("SELECT `dislike` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
                    $resDislike=$wpdb->get_results($numDislike);   
                    echo json_encode(array("like"=>$resLike[0]->like,"dislike"=>$resDislike[0]->dislike,"statLike"=>'Liked',"statDislike"=>'Dislike'));
                }
        die();
    }elseif(isset($_POST['task']) && $_POST['task']=="dislike"){
            $huge_it_ip='';
        if(!empty($_SERVER['HTTP_CLIENT_IP'])){
          $huge_it_ip=$_SERVER['HTTP_CLIENT_IP'];
        }
        elseif(!empty($_SERVER['HTTP_X_FORWARDED_FOR'])){
          $huge_it_ip=$_SERVER['HTTP_X_FORWARDED_FOR'];
        }
        else{
          $huge_it_ip=$_SERVER['REMOTE_ADDR'];
        }
        global $wpdb;
        $num=$wpdb->prepare("SELECT `image_status`,`ip` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d",(int)$_POST['image_id']);
        $num2=$wpdb->prepare("SELECT `image_status`,`ip` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip` = '".$huge_it_ip."'",(int)$_POST['image_id']);  
        $res=$wpdb->get_results($num); 
        $res2=$wpdb->get_results($num,ARRAY_A);
        $res3=$wpdb->get_row($num2);
        $num3=$wpdb->prepare("SELECT `image_status`,`ip`,`cook` FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook` = '".$_POST['cook']."'",(int)$_POST['image_id']);
        $res4=$wpdb->get_row($num3);
            $resIP='';
            for ($i=0; $i <count($res2) ; $i++) {                 
                $resIP.=$res2[$i]['ip'].'|';                           
            }
            $arrIP = explode("|", $resIP);
        if(!isset($res3)&&!isset($res4)){
            $wpdb->query($wpdb->prepare("INSERT INTO ".$wpdb->prefix."huge_itgallery_like_dislike (`image_id`,`image_status`,`ip`,`cook`) VALUES ( %d, 'disliked', '".$huge_it_ip."',%s)",(int)$_POST['image_id'],$_POST['cook']));
            $wpdb->query($wpdb->prepare("UPDATE ".$wpdb->prefix."huge_itgallery_images SET  `dislike` = `dislike`+1 WHERE id = %d ",(int)$_POST['image_id']));
            $numDislike=$wpdb->prepare("SELECT `dislike` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
            $resDislike=$wpdb->get_results($numDislike); 
            $numLike=$wpdb->prepare("SELECT `like` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
            $resLike=$wpdb->get_results($numLike);   
            echo json_encode(array("dislike"=>$resDislike[0]->dislike,"statDislike"=>'Disliked'));  
        }elseif((isset($res3)&&$res3->image_status=='disliked'&&$res3->ip==$huge_it_ip)||(isset($res4)&&$res4->image_status=='disliked'&&$res4->cook==$_POST['cook'])){
              if(isset($res3)&&$res3->image_status=='disliked'&&$res3->ip==$huge_it_ip){
                       $wpdb->query($wpdb->prepare("DELETE FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip`='".$huge_it_ip."'",(int)$_POST['image_id'])); 
               }elseif(isset($res4)&&$res4->image_status=='disliked'&&$res4->cook==$_POST['cook']){
                   $wpdb->query($wpdb->prepare("DELETE FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook`='".$_POST['cook']."'",(int)$_POST['image_id'])); 
               }
            $wpdb->query($wpdb->prepare("UPDATE ".$wpdb->prefix."huge_itgallery_images SET  `dislike` = `dislike`-1 WHERE id = %d ",(int)$_POST['image_id']));
            $numDislike=$wpdb->prepare("SELECT `dislike` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
            $resDislike=$wpdb->get_results($numDislike); 
            $numLike=$wpdb->prepare("SELECT `like` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
            $resLike=$wpdb->get_results($numLike);   
            echo json_encode(array("dislike"=>$resDislike[0]->dislike,"statDislike"=>'Dislike'));
        }elseif((isset($res3)&&$res3->image_status=='liked'&&$res3->ip==$huge_it_ip)||(isset($res4)&&$res4->image_status=='liked'&&$res4->cook==$_POST['cook'])){
            if(isset($res3)&&$res3->image_status=='liked'&&$res3->ip==$huge_it_ip){
                        $wpdb->query($wpdb->prepare("DELETE FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `ip`='".$huge_it_ip."'",(int)$_POST['image_id']));
            }elseif(isset($res4)&&$res4->image_status=='liked'&&$res4->cook==$_POST['cook']){
               $wpdb->query($wpdb->prepare("DELETE FROM ".$wpdb->prefix."huge_itgallery_like_dislike WHERE image_id = %d AND `cook`='".$_POST['cook']."'",(int)$_POST['image_id'])); 
            }
            $wpdb->query($wpdb->prepare("INSERT INTO ".$wpdb->prefix."huge_itgallery_like_dislike (`image_id`,`image_status`,`ip`,`cook`) VALUES ( %d, 'disliked', '".$huge_it_ip."',%s)",(int)$_POST['image_id'],$_POST['cook']));
            $wpdb->query($wpdb->prepare("UPDATE ".$wpdb->prefix."huge_itgallery_images SET  `dislike` = `dislike`+1 WHERE id = %d ",(int)$_POST['image_id']));
            $wpdb->query($wpdb->prepare("UPDATE ".$wpdb->prefix."huge_itgallery_images SET  `like` = `like`-1 WHERE id = %d ",(int)$_POST['image_id']));
            $numDislike=$wpdb->prepare("SELECT `dislike` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
            $resDislike=$wpdb->get_results($numDislike); 
            $numLike=$wpdb->prepare("SELECT `like` FROM ".$wpdb->prefix."huge_itgallery_images WHERE id = %d LIMIT 1",(int)$_POST['image_id']);  
            $resLike=$wpdb->get_results($numLike);   
            echo json_encode(array("like"=>$resLike[0]->like,"dislike"=>$resDislike[0]->dislike,"statLike"=>'Like',"statDislike"=>'Disliked'));
        }
        die();
    }
}
function add_gallery_my_custom_button($context) {
  $img = plugins_url( '/images/post.button.png' , __FILE__ );
  $container_id = 'huge_it_gallery';
  $title = 'Select Huge IT gallery to insert into post';
  $context .= '<a class="button thickbox" title="Select gallery to insert into post"    href="#TB_inline?width=400&inlineId='.$container_id.'">
        <span class="wp-media-buttons-icon" style="background: url('.$img.'); background-repeat: no-repeat; background-position: left bottom;"></span>
    Add gallery
    </a>';
  return $context;
}
function add_gallery_inline_popup_content() {
?>
<script type="text/javascript">
                jQuery(document).ready(function() {
                  jQuery('#hugeitgalleryinsert').on('click', function() {
                    var id = jQuery('#huge_it_gallery-select option:selected').val();
                    window.send_to_editor('[huge_it_gallery id="' + id + '"]');
                    tb_remove();
                  })
                });
</script>
<div id="huge_it_gallery" style="display:none;">
  <h3><?php echo __('Select Huge IT Gallery to insert into post', 'gallery-images'); ?></h3>
  <?php 
      global $wpdb;
      $query="SELECT * FROM ".$wpdb->prefix."huge_itgallery_gallerys order by id ASC";
               $shortcodegallerys=$wpdb->get_results($query);
               ?>
 <?php  if (count($shortcodegallerys)) {
                            echo "<select id='huge_it_gallery-select'>";
                            foreach ($shortcodegallerys as $shortcodegallery) {
                                echo "<option value='".$shortcodegallery->id."'>".$shortcodegallery->name."</option>";
                            }
                            echo "</select>";
                            echo "<button class='button primary' id='hugeitgalleryinsert'>Insert gallery</button>";
                        } else {
                            echo "No slideshows found", "huge_it_gallery";
                        }
                        ?>
</div>
<?php
}
///////////////////////////////////shortcode update/////////////////////////////////////////////
add_action('init', 'hugesl_gallery_do_output_buffer');
function hugesl_gallery_do_output_buffer() {
        ob_start();
}

function huge_it_gallery_images_list_shotrcode($atts)
{
    extract(shortcode_atts(array(
        'id' => 'no huge_it gallery',
    ), $atts));
    return huge_it_gallery_images_list($atts['id']);
}
/////////////// Filter gallery
function gallery_after_search_results($query)
{
    global $wpdb;
    if (isset($_REQUEST['s']) && $_REQUEST['s']) {
        $serch_word = htmlspecialchars(($_REQUEST['s']));
        $query = str_replace($wpdb->prefix . "posts.post_content", gen_string_gallery_search($serch_word, $wpdb->prefix . 'posts.post_content') . " " . $wpdb->prefix . "posts.post_content", $query);
    }
    return $query;
}
add_filter('posts_request', 'gallery_after_search_results');
function gen_string_gallery_search($serch_word, $wordpress_query_post)
{
    $string_search = '';
    global $wpdb;
    if ($serch_word) {
        $rows_gallery = $wpdb->get_results($wpdb->prepare("SELECT * FROM " . $wpdb->prefix . "huge_itgallery_gallerys WHERE (description LIKE %s) OR (name LIKE %s)", '%' . $serch_word . '%', "%" . $serch_word . "%"));
        $count_cat_rows = count($rows_gallery);
        for ($i = 0; $i < $count_cat_rows; $i++) {
            $string_search .= $wordpress_query_post . ' LIKE \'%[huge_it_gallery id="' . $rows_gallery[$i]->id . '" details="1" %\' OR ' . $wordpress_query_post . ' LIKE \'%[huge_it_gallery id="' . $rows_gallery[$i]->id . '" details="1"%\' OR ';
        }
        $rows_gallery = $wpdb->get_results($wpdb->prepare("SELECT * FROM " . $wpdb->prefix . "huge_itgallery_gallerys WHERE (name LIKE %s)","'%" . $serch_word . "%'"));
        $count_cat_rows = count($rows_gallery);
        for ($i = 0; $i < $count_cat_rows; $i++) {
            $string_search .= $wordpress_query_post . ' LIKE \'%[huge_it_gallery id="' . $rows_gallery[$i]->id . '" details="0"%\' OR ' . $wordpress_query_post . ' LIKE \'%[huge_it_gallery id="' . $rows_gallery[$i]->id . '" details="0"%\' OR ';
        }
        $rows_single = $wpdb->get_results($wpdb->prepare("SELECT * FROM " . $wpdb->prefix . "huge_itgallery_images WHERE name LIKE %s","'%" . $serch_word . "%'"));
        $count_sing_rows = count($rows_single);
        if ($count_sing_rows) {
            for ($i = 0; $i < $count_sing_rows; $i++) {
                $string_search .= $wordpress_query_post . ' LIKE \'%[huge_it_gallery_Product id="' . $rows_single[$i]->id . '"]%\' OR ';
            }
        }
    }
    return $string_search;
}
///////////////////// end filter
add_shortcode('huge_it_gallery', 'huge_it_gallery_images_list_shotrcode');
function   huge_it_gallery_images_list($id)
{
    require_once("Front_end/gallery_front_end_view.php");
    require_once("Front_end/gallery_front_end_func.php");
    if (isset($_GET['product_id'])) {
        if (isset($_GET['view'])) {
            if ($_GET['view'] == 'huge_itgallery') {
                return showPublishedgallery_1($id);
            } else {
                return front_end_single_product($_GET['product_id']);
            }
        } else {
            return front_end_single_product($_GET['product_id']);
        }
    } else {
        return showPublishedgallery_1($id);
    }
}
add_filter('admin_head', 'huge_it_gallery_ShowTinyMCE');
function huge_it_gallery_ShowTinyMCE()
{
    // conditions here
    wp_enqueue_script('common');
    wp_enqueue_script('jquery-color');
    wp_print_scripts('editor');
    if (function_exists('add_thickbox')) add_thickbox();
    wp_print_scripts('media-upload');
    if (version_compare(get_bloginfo('version'), 3.3) < 0) {
        if (function_exists('wp_tiny_mce')) wp_tiny_mce();
    }
    wp_admin_css();
    wp_enqueue_script('utils');
    do_action("admin_print_styles-post-php");
    do_action('admin_print_styles');
}
function all_frontend_scripts_and_styles() {
    if ( wp_script_is( 'jquery', 'registered' ) )
        wp_enqueue_script('jquery');
    else { 
        wp_register_script('jquery', 'https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js', __FILE__ ); 
        wp_enqueue_script('jquery');
    }
    wp_register_script( 'jquery.gicolorbox-js', plugins_url('/js/jquery.colorbox.js', __FILE__), array('jquery'),'1.0.0',true  ); 
    wp_enqueue_script( 'jquery.gicolorbox-js' );
    wp_register_script( 'gallery-hugeitmicro-min-js', plugins_url('/js/jquery.hugeitmicro.min.js', __FILE__), array('jquery'),'1.0.0',true  ); 
    wp_enqueue_script( 'gallery-hugeitmicro-min-js' );
    wp_register_style( 'style2-os-css', plugins_url('/style/style2-os.css', __FILE__) );   
    wp_enqueue_style( 'style2-os-css' );
    wp_register_style( 'lightbox-css', plugins_url('/style/lightbox.css', __FILE__) );   
    wp_enqueue_style( 'lightbox-css' );
    wp_register_style( 'fontawesome-css', plugins_url('/style/css/font-awesome.css', __FILE__) );   
    wp_enqueue_style( 'fontawesome-css' );
}
add_action('wp_enqueue_scripts', 'all_frontend_scripts_and_styles');
add_action('admin_menu', 'huge_it_gallery_options_panel');
function huge_it_gallery_options_panel()
{
    $page_cat = add_menu_page('Theme page title', 'Huge IT Gallery', 'delete_pages', 'gallerys_huge_it_gallery', 'gallerys_huge_it_gallery', plugins_url('images/huge_it_galleryLogoHover -for_menu.png', __FILE__));
    $page_option = add_submenu_page('gallerys_huge_it_gallery', 'General Options', 'General Options', 'manage_options', 'Options_gallery_styles', 'Options_gallery_styles');
    $lightbox_options = add_submenu_page('gallerys_huge_it_gallery', 'Lightbox Options', 'Lightbox Options', 'manage_options', 'Options_gallery_lightbox_styles', 'Options_gallery_lightbox_styles');
    add_submenu_page('gallerys_huge_it_gallery', 'Licensing', 'Licensing', 'manage_options', 'huge_it_imagegallery_Licensing', 'huge_it_imagegallery_Licensing');
    add_submenu_page('gallerys_huge_it_gallery', 'Featured Plugins', 'Featured Plugins', 'manage_options', 'huge_it__gallery_featured_plugins', 'huge_it__gallery_featured_plugins');
    add_action('admin_print_styles-' . $page_cat, 'huge_it_gallery_admin_script');
    add_action('admin_print_styles-' . $page_option, 'huge_it_gallery_option_admin_script');
    add_action('admin_print_styles-' . $lightbox_options, 'huge_it_gallery_option_admin_script');
}
function huge_it__gallery_featured_plugins()
{
    include_once("admin/huge_it_featured_plugins.php");
}
function huge_it_imagegallery_Licensing(){
    ?>
    <div style="width:95%">
    <p>
    This plugin is the non-commercial version of the Huge IT Image Gallery. If you want to customize to the styles and colors of your website,than you need to buy a license.
Purchasing a license will add possibility to customize the general options of the Huge IT Image Gallery. 
 </p>
<br /><br />
<a href="http://huge-it.com/wordpress-gallery/" class="button-primary" target="_blank">Purchase a License</a>
<br /><br /><br />
<p>After the purchasing the commercial version follow this steps:</p>
<ol>
    <li>Deactivate Huge IT Image Gallery Plugin</li>
    <li>Delete Huge IT Image Gallery Plugin</li>
    <li>Install the downloaded commercial version of the plugin</li>
</ol>
</div>
<?php
    }
function gallery_sliders_huge_it_slider()
{
    require_once("admin/gallery_slider_func.php");
    require_once("admin/gallery_slider_view.php");
    if (!function_exists('print_html_nav'))
        require_once("gallery_function/html_gallery_func.php");
    if (isset($_GET["task"]))
        $task = $_GET["task"]; 
    else
        $task = '';
    if (isset($_GET["id"]))
        $id = $_GET["id"];
    else
        $id = 0;
    global $wpdb;
    switch ($task) {
        case 'add_cat':
            add_slider();
            break;
        case 'add_shortcode_post':
            add_shortcode_post();
            break;
        case 'popup_posts':
            if ($id)
                popup_posts($id);
            break;
        case 'gallery_video':
            if ($id)
                gallery_video($id);
            else {
                $id = $wpdb->get_var("SELECT MAX( id ) FROM " . $wpdb->prefix . "huge_itgallery_gallerys");
                gallery_video($id);
            }
            break;
        case 'edit_cat':
            if ($id)
                editslider($id);
            else {
                $id = $wpdb->get_var("SELECT MAX( id ) FROM " . $wpdb->prefix . "huge_itgallery_gallerys");
                editslider($id);
            }
            break;
        case 'save':
            if ($id)
                apply_cat($id);
        case 'apply':
            if ($id) {
                apply_cat($id);
                editslider($id);
            } 
            break;
        case 'remove_cat':
            removeslider($id);
            showslider();
            break;
        default:
            showslider();
            break;
    }
}
function gallery_Options_slider_styles()
{
    require_once("admin/gallery_slider_options_func.php");
    require_once("admin/gallery_slider_options_view.php");
    if (isset($_GET['task']))
        if ($_GET['task'] == 'save')
            save_styles_options();
    showStyles();
}
//////////////////////////Huge it Slider ///////////////////////////////////////////
function huge_it_gallery_admin_script()
{
    wp_enqueue_media();
    wp_enqueue_style("jquery_ui", "http://code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css", FALSE);
    wp_enqueue_style("jquery_ui", plugins_url("style/jquery-ui.css", __FILE__), FALSE);
    wp_enqueue_style("admin_css", plugins_url("style/admin.style.css", __FILE__), FALSE);
    wp_enqueue_script("admin_js", plugins_url("js/admin.js", __FILE__), FALSE);
}
function huge_it_gallery_option_admin_script()
{
    wp_enqueue_script("jquery_old", "http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js", FALSE);
    wp_enqueue_script("simple_slider_js",  plugins_url("js/simple-slider.js", __FILE__), FALSE);
    wp_enqueue_style("simple_slider_css", plugins_url("style/simple-slider_sl.css", __FILE__), FALSE);
    wp_enqueue_style("admin_css", plugins_url("style/admin.style.css", __FILE__), FALSE);
    wp_enqueue_script("admin_js", plugins_url("js/admin.js", __FILE__), FALSE);
    wp_enqueue_script('param_block2', plugins_url("elements/jscolor/jscolor.js", __FILE__));
}
function gallerys_huge_it_gallery()
{
    require_once("admin/gallery_func.php");
    require_once("admin/gallery_view.php");
    if (!function_exists('print_html_nav'))
        require_once("gallery_function/html_gallery_func.php");
    if (isset($_GET["task"]))
        $task = $_GET["task"]; 
    else
        $task = '';
    if (isset($_GET["id"]))
        $id = $_GET["id"];
    else
        $id = 0;
    global $wpdb;
    switch ($task) {
        case 'add_cat':
            add_gallery();
            break;
        case 'gallery_video':
            if ($id)
                gallery_video($id);
            else {
                $id = $wpdb->get_var("SELECT MAX( id ) FROM " . $wpdb->prefix . "huge_itgallery_gallerys");
                gallery_video($id);
            }
            break;
        case 'edit_cat':
            if ($id)
                editgallery($id);
            else {
                $id = $wpdb->get_var("SELECT MAX( id ) FROM " . $wpdb->prefix . "huge_itgallery_gallerys");
                editgallery($id);
            }
            break;
        case 'save':
            if ($id)
                apply_cat($id);
        case 'apply':
            if ($id) {
                apply_cat($id);
                editgallery($id);
            } 
            break;
        case 'remove_cat':
            removegallery($id);
            showgallery();
            break;
        default:
            showgallery();
            break;
    }
}
function Options_gallery_styles()
{
    require_once("admin/gallery_Options_func.php");
    require_once("admin/gallery_Options_view.php");
    if (isset($_GET['task']))
        if ($_GET['task'] == 'save')
            save_styles_options();
    showStyles();
}
function Options_gallery_lightbox_styles()
{
    require_once("admin/gallery_lightbox_func.php");
    require_once("admin/gallery_lightbox_view.php");
    if (isset($_GET['task']))
        if ($_GET['task'] == 'save')
            save_styles_options();
    showStyles();
}
/**
 * Huge IT Widget
 */
class Huge_it_gallery_Widget extends WP_Widget {
    public function __construct() {
        parent::__construct(
            'Huge_it_gallery_Widget', 
            'Huge IT gallery', 
            array( 'description' => __( 'Huge IT gallery', 'huge_it_gallery' ), ) 
        );
    }
    public function widget( $args, $instance ) {
        extract($args);
        if (isset($instance['gallery_id'])) {
            $gallery_id = $instance['gallery_id'];
            $title = apply_filters( 'widget_title', $instance['title'] );
            echo $before_widget;
            if ( ! empty( $title ) )
                echo $before_title . $title . $after_title;
            echo do_shortcode("[huge_it_gallery id={$gallery_id}]");
            echo $after_widget;
        }
    }
    public function update( $new_instance, $old_instance ) {
        $instance = array();
        $instance['gallery_id'] = strip_tags( $new_instance['gallery_id'] );
        $instance['title'] = strip_tags( $new_instance['title'] );
        return $instance;
    }
    public function form( $instance ) {
        $selected_gallery = 0;
        $title = "";
        $gallerys = false;
        if (isset($instance['gallery_id'])) {
            $selected_gallery = $instance['gallery_id'];
        }
        if (isset($instance['title'])) {
            $title = $instance['title'];
        }
        ?>
        <p>
                <p>
                    <label for="<?php echo $this->get_field_id( 'title' ); ?>"><?php _e( 'Title:' ); ?></label> 
                    <input class="widefat" id="<?php echo $this->get_field_id( 'title' ); ?>" name="<?php echo $this->get_field_name( 'title' ); ?>" type="text" value="<?php echo esc_attr( $title ); ?>" />
                </p>
                <label for="<?php echo $this->get_field_id('gallery_id'); ?>"><?php _e('Select gallery:', 'huge_it_gallery'); ?></label> 
                <select id="<?php echo $this->get_field_id('gallery_id'); ?>" name="<?php echo $this->get_field_name('gallery_id'); ?>">
                <?php
                 global $wpdb;
                $query="SELECT * FROM ".$wpdb->prefix."huge_itgallery_gallerys ";
                $rowwidget=$wpdb->get_results($query);
                foreach($rowwidget as $rowwidgetecho){
                ?>
                <?php if(isset($instance['gallery_id'])){ ?>
                    <option value="<?php echo $rowwidgetecho->id; ?>"><?php echo $rowwidgetecho->name; ?></option>
                <?php }
                    else { ?> <option value="<?php echo $rowwidgetecho->id; ?>"><?php echo $rowwidgetecho->name; ?></option> <?php }
                    } ?>
                </select>
        </p>
        <?php 
    }
}
add_action('widgets_init', 'register_Huge_it_gallery_Widget');  
function register_Huge_it_gallery_Widget() {  
    register_widget('Huge_it_gallery_Widget'); 
}
//////////////////////////////////////////////////////                                             ///////////////////////////////////////////////////////
//////////////////////////////////////////////////////               Activate gallery                     ///////////////////////////////////////////////////////
//////////////////////////////////////////////////////                                             ///////////////////////////////////////////////////////
//////////////////////////////////////////////////////                                             ///////////////////////////////////////////////////////
function huge_it_gallery_activate()
{
    global $wpdb;
/// creat database tables
    $sql_huge_itgallery_images = "
CREATE TABLE IF NOT EXISTS `" . $wpdb->prefix . "huge_itgallery_images` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `gallery_id` varchar(200) DEFAULT NULL,
  `description` text,
  `image_url` text,
  `sl_url` varchar(128) DEFAULT NULL,
  `sl_type` text NOT NULL,
  `link_target` text NOT NULL,
  `ordering` int(11) NOT NULL,
  `published` tinyint(4) unsigned DEFAULT NULL,
  `published_in_sl_width` tinyint(4) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
)   DEFAULT CHARSET=utf8 AUTO_INCREMENT=5";
    $sql_huge_itgallery_like_dislike = "
CREATE TABLE IF NOT EXISTS `" . $wpdb->prefix . "huge_itgallery_like_dislike` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_id` int(11) NOT NULL,
  `image_status` varchar(10) NOT NULL,
  `ip` varchar(35) NOT NULL,
  `cook` varchar(15) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
)   DEFAULT CHARSET=utf8 AUTO_INCREMENT=10";
    $sql_huge_itgallery_gallerys = "
CREATE TABLE IF NOT EXISTS `" . $wpdb->prefix . "huge_itgallery_gallerys` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `sl_height` int(11) unsigned DEFAULT NULL,
  `sl_width` int(11) unsigned DEFAULT NULL,
  `pause_on_hover` text,
  `gallery_list_effects_s` text,
  `description` text,
  `param` text,
  `sl_position` text NOT NULL,
  `ordering` int(11) NOT NULL,
  `published` text,
   `huge_it_sl_effects` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
)  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ";
    $table_name = $wpdb->prefix . "huge_itgallery_images";
    $sql_2 = "
INSERT INTO 
`" . $table_name . "` (`id`, `name`, `gallery_id`, `description`, `image_url`, `sl_url`, `sl_type`, `link_target`, `ordering`, `published`, `published_in_sl_width`) VALUES
(1, 'Rocky Balboa', '1', '<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. </p><p>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>', '".plugins_url("Front_images/projects/1.jpg", __FILE__)."', 'http://huge-it.com/wordpress-theme-company/', 'image', 'on', 0, 1, NULL),
(2, 'Skiing alone', '1', '<ul><li>lorem ipsumdolor sit amet</li><li>lorem ipsum dolor sit amet</li></ul><p>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>', '".plugins_url("Front_images/projects/2.jpg", __FILE__)."', 'http://huge-it.com/wordpress-theme-company/', 'image', 'on', 1, 1, NULL),
(3, 'Summer dreams', '1', '<h6>Lorem Ipsum </h6><p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. </p><p>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p><ul><li>lorem ipsum</li><li>dolor sit amet</li><li>lorem ipsum</li><li>dolor sit amet</li></ul>', '".plugins_url("Front_images/projects/3.jpg", __FILE__)."', 'http://huge-it.com/wordpress-theme-company/', 'image', 'on', 2, 1, NULL),
(4, 'Mr. Cosmo Kramer', '1', '<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. </p><h6>Dolor sit amet</h6><p>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>', '".plugins_url("Front_images/projects/4.jpg", __FILE__)."', 'http://huge-it.com/wordpress-gallery/', 'image', 'on', 3, 1, NULL),
(5, 'Edgar Allan Poe', '1', '<h6>Lorem Ipsum</h6><p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>', '".plugins_url("Front_images/projects/5.jpg", __FILE__)."', 'http://huge-it.com/wordpress-gallery/', 'image', 'on', 4, 1, NULL),
(6, 'Fix the moment !', '1', '<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. </p><p>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>', '".plugins_url("Front_images/projects/6.jpg", __FILE__)."', 'http://huge-it.com/wordpress-gallery/', 'image', 'on', 5, 1, NULL),
(7, 'Lions eyes', '1', '<h6>Lorem Ipsum</h6><p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. </p><p>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>', '".plugins_url("Front_images/projects/7.jpg", __FILE__)."', 'http://huge-it.com/wordpress-gallery/', 'image', 'on', 6, 1, NULL),
(8, 'Photo with exposure', '1', '<h6>Lorem Ipsum </h6><p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. </p><p>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p><ul><li>lorem ipsum</li><li>dolor sit amet</li><li>lorem ipsum</li><li>dolor sit amet</li></ul>', '".plugins_url("Front_images/projects/8.jpg", __FILE__)."', 'http://huge-it.com/wordpress-theme-company/', 'image', 'on', 7, 1, NULL),
(9, 'Travel with music', '1', '<h6>Lorem Ipsum </h6><p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. </p><p>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p><ul><li>lorem ipsum</li><li>dolor sit amet</li><li>lorem ipsum</li><li>dolor sit amet</li></ul>', '".plugins_url("Front_images/projects/9.jpg", __FILE__)."', 'http://huge-it.com/wordpress-theme-company/', 'image', 'on', 7, 1, NULL)";
    $table_name = $wpdb->prefix . "huge_itgallery_gallerys";
    $sql_3 = "
INSERT INTO `$table_name` (`id`, `name`, `sl_height`, `sl_width`, `pause_on_hover`, `gallery_list_effects_s`, `description`, `param`, `sl_position`, `ordering`, `published`, `huge_it_sl_effects`) VALUES
(1, 'My First Gallery', 375, 600, 'on', 'random', '4000', '1000', 'center', 1, '300', '5')";
    $wpdb->query($sql_huge_itgallery_images);
    $wpdb->query($sql_huge_itgallery_gallerys);
    $wpdb->query($sql_huge_itgallery_like_dislike);
    if (!$wpdb->get_var("select count(*) from " . $wpdb->prefix . "huge_itgallery_images")) {
      $wpdb->query($sql_2);
    }
    if (!$wpdb->get_var("select count(*) from " . $wpdb->prefix . "huge_itgallery_gallerys")) {
      $wpdb->query($sql_3);
    }
    ////////////////////////////////////////
  $imagesAllFieldsInArray2 = $wpdb->get_results("DESCRIBE " . $wpdb->prefix . "huge_itgallery_gallerys", ARRAY_A);
        $fornewUpdate = 0;
        foreach ($imagesAllFieldsInArray2 as $portfoliosField2) {
            if ($portfoliosField2['Field'] == 'display_type') {
                $fornewUpdate = 1;
            }
        }
        if($fornewUpdate != 1){
            $wpdb->query("ALTER TABLE ".$wpdb->prefix."huge_itgallery_gallerys ADD display_type integer DEFAULT '2' ");
            $wpdb->query("ALTER TABLE ".$wpdb->prefix."huge_itgallery_gallerys ADD content_per_page integer DEFAULT '5' ");
        }   
        //////////////////////////////////////////////
        $imagesAllFieldsInArray3 = $wpdb->get_results("DESCRIBE " . $wpdb->prefix . "huge_itgallery_images", ARRAY_A);
        $fornewUpdate2 = 0;
        foreach ($imagesAllFieldsInArray3 as $portfoliosField3) {
            if ($portfoliosField3['Field'] == 'sl_url'  &&  $portfoliosField3['Type'] == 'text') {
               $fornewUpdate2=1;
            }
        }
        if($fornewUpdate2 != 1){
            $wpdb->query("ALTER TABLE ".$wpdb->prefix."huge_itgallery_images CHANGE sl_url sl_url TEXT CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL");
        } 
          //////////////////////////////////////// 
      //ADDING LIKE/DISLIKE COLUMNS  
    ///////////////////////////////////////////////////////////////////////
        $imagesAllFieldsInArray4 = $wpdb->get_results("DESCRIBE " . $wpdb->prefix . "huge_itgallery_images", ARRAY_A);
        $fornewUpdate3 = 0;
        foreach ($imagesAllFieldsInArray4 as $portfoliosField4) {
            if ($portfoliosField4['Field'] == 'like') {
               $fornewUpdate3=1;
            }
        }
        if($fornewUpdate3 != 1){
            $wpdb->query("ALTER TABLE ".$wpdb->prefix."huge_itgallery_images  ADD `like` INT NOT NULL DEFAULT '0' AFTER `published_in_sl_width`");
            $wpdb->query("ALTER TABLE ".$wpdb->prefix."huge_itgallery_images  ADD `dislike` INT NOT NULL DEFAULT '0' AFTER `like`");
        }
      //ADDING Rating COLUMNS  
        $imagesAllFieldsInArray5 = $wpdb->get_results("DESCRIBE " . $wpdb->prefix . "huge_itgallery_gallerys", ARRAY_A);
        $fornewUpdate4 = 0;
        foreach ($imagesAllFieldsInArray5 as $portfoliosField5) {
            if ($portfoliosField5['Field'] == 'rating') {
               $fornewUpdate4=1;
            }
        }
        if($fornewUpdate4 != 1){
            $wpdb->query("ALTER TABLE ".$wpdb->prefix."huge_itgallery_gallerys  ADD `rating` VARCHAR( 15 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  'off'");
        }
        ////////////////////////////////////////////////////////////////////////
}
register_activation_hook(__FILE__, 'huge_it_gallery_activate');
require_once( ABSPATH . 'wp-admin/includes/plugin.php' );
$plugin_info = get_plugin_data( ABSPATH . 'wp-content/plugins/gallery-images/gallery-images.php' );
if($plugin_info['Version'] > '1.6.3'){
    huge_it_gallery_activate();
}