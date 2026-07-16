/*
Copyright (c) 2003-2011, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.editorConfig = function( config )
{
	// Define changes to default configuration here. For example:
	config.language = 'zh-cn'; //配置语言
	config.uiColor = '#FFF'; //背景颜色
	//config.width = 600; //宽度
	config.height = 355; //高度
	config.fullPage = false; 
	config.startupMode ='source'; 
	//工具栏
	config.toolbar =
	[
	      ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
	      ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
	      ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
	      ['Link','Unlink','Anchor'],
	      ['Image','Table','HorizontalRule','Smiley','SpecialChar','PageBreak'],
	      //['Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak'],
	      ['Styles','Format','Font','FontSize'],
	      ['TextColor','BGColor','Source']
	  //    ['Maximize', 'ShowBlocks','-','Source','-','Undo','Redo']
	];
	
};
