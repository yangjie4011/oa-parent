<%@page pageEncoding="UTF-8"%>  
	<form id="picForm">
	    <div class="input-list">
	        <div class="h">照片</div>
	        <div class="m">
	            <div class="moudle-ll">
	                <div class="img">
	                	<form id="empPicForm" enctype="multipart/form-data"> 
	                		<input type="hidden" name="id" id="empId"/>
		                     <!--替换背景图片来替换头像-->
		                     <div class="bg" id="ePic"></div>
		                     <div id ="showEmpPic" style = "display:none">
			                     <input id="empPic" name="file" type="file" onclick="uploadPic(this)"/>
			                     <p>点击更换</p>
		                     </div>
	                    </form>
	                    
	              <!-- <p class="name"><span id="cnName"></span></p>
	              <p><span id="positionName"></span>/<span id="managerName"></span></p>
	              <p>汇报人  <span id="departName"></p> -->
	           </div>
	            </div>
	        </div>
	    </div>
    </form>
