	

// Se definen las variables globales.
var server;



// A continuación  la ejecucion luego de cargada la pagina
$(document).ready(function() {

	//setInterval('validarEstadoLogin()',300000);
	obtenerTiempo();
	setInterval('validarVigenciaLogueo()',600000);
});


function validarVigenciaLogueo()
{
	var d = new Date();
	
	var respuesta ='';
	$.ajax({ 
	   	url: server + 'ValidarUsuarioAplicacion', 
	   	dataType: 'json',
	   	type: 'post', 
	   	async: false, 
	   	success: function(data){
			    respuesta =  data[0].respuesta;		
		} 
	});
	switch(respuesta)
	{
		case 'OK':
				break;
		case 'OKA':
				break;	
		default:
				location.href = server +"Index.html";
		    	break;
	}
		    		
}

function obtenerTiempo()
{
	$.ajax({ 
		    	url: server + 'CRUDTiempoPedido?idoperacion=2', 
		    	dataType: 'json',
		    	type: 'get', 
		    	async: false, 
		    	success: function(data){
						    	var tiempoactual = data.tiempopedido;
						    	console.log(tiempoactual);
						    	var valorLabel;
						    	if (tiempoactual <= 50)
						    	{
						    		valorLabel =  '<h1 style="color:#55FF33;">'+ tiempoactual + " MINUTOS" + '</h1>';
						    	}else if (tiempoactual > 50  & tiempoactual <= 80)
						    	{
						    		valorLabel =  '<h1 style="color:#AFFF33;">'+ tiempoactual + " MINUTOS" + '</h1>';
						    	}else if (tiempoactual > 80)
						    	{
						    		valorLabel =  '<h1 style="color:#FF5533;">'+ tiempoactual + " MINUTOS" + '</h1>';
						    	}

						    	$('#tiempoactual').html(valorLabel);

				} 
			});
}

function ConfirmarTiempo()
{
	var nuevotiempo = $('#nuevotiempo').val();
	$.ajax({ 
		    	url: server + 'CRUDTiempoPedido?idoperacion=1&nuevotiempo='+nuevotiempo, 
		    	dataType: 'json',
		    	type: 'get', 
		    	async: false, 
		    	success: function(data){
						    respuesta =  data.resultado;	
						    if(respuesta == true)	
						    {
						    	var valorLabel;
						    	if (nuevotiempo <= 50)
						    	{
						    		valorLabel =  '<h1 style="color:#55FF33;">'+ nuevotiempo + " MINUTOS" +'</h1>';
						    	}else if (nuevotiempo > 50  & nuevotiempo <= 80)
						    	{
						    		valorLabel =  '<h1 style="color:#AFFF33;">'+ nuevotiempo + " MINUTOS" +'</h1>';
						    	}else if (nuevotiempo > 80)
						    	{
						    		valorLabel =  '<h1 style="color:#FF5533;">'+ nuevotiempo + " MINUTOS" +'</h1>';
						    	}
						    	
						    	$('#tiempoactual').html(valorLabel);
						    	$('#nuevotiempo').val('');
						    	//Obtenemos la tienda
								var idtienda;
								var serverContact;
								$.ajax({ 
									    	url: server + 'GetTienda?idoperacion=1&', 
									    	dataType: 'json',
									    	type: 'get', 
									    	async: false, 
									    	success: function(data1){
													    idtienda =  data1[0].idtienda;
													    serverContact = data1[0].urlcontact;
													    console.log(data1[0]);
											} 
								});
								//Enviamos Actualización a la tienda
								$.ajax({ 
									    	url: serverContact + 'CRUDTiempoPedido?idoperacion=1&nuevotiempo='+ nuevotiempo + "&idtienda=" + idtienda , 
									    	dataType: 'json',
									    	type: 'get', 
									    	async: false, 
									    	success: function(data2){
													    
											} 
								});
						    	alert("Se ha actualizado el tiempo de Entrega de Pedidos para la Tienda");
						    }

				} 
			});

}

function validarEstadoLogin()
{
			var getImport; 
			var getContent;
			//Obtenemos el valor de la variable server, con base en la URL digitada
			var loc = window.location;
			var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
			server = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
			var respuesta ='';
			$.ajax({ 
		    	url: server + 'ValidarUsuarioAplicacion', 
		    	dataType: 'text',
		    	type: 'post', 
		    	async: false, 
		    	success: function(data){
						    respuesta =  data;		
				} 
			});
			switch(respuesta)
		    		{
		    			case 'OK':
		    				getImport = document.querySelector('#Menu-file');
		    				break;
		    			case 'OKA':
		    				getImport = document.querySelector('#MenuAdm-file');
		    				break;	
		    			default:
		    				location.href = server +"Index.html";
		    				break;
		    		}
		    getContent = getImport.import.querySelector('#menuprincipal');
			document.body.appendChild(document.importNode(getContent, true));
}