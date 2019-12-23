 var dtPedidos;
 var server;
 var rutaMapa;
 var latitudTienda;
 var longitudTienda;

// A continuación  la ejecucion luego de cargada la pagina
$(document).ready(function() {


    dtPedidos = $('#grid-pedidos').DataTable( {
            "aoColumns": [
            { "mData": "idpedidotienda" },
            { "mData": "fechapedido" },
            { "mData": "nombres" },
            { "mData": "tipopedido" },
            { "mData": "estadopedido" },
            { "mData": "direccion" },
            { "mData": "idtipopedido", "visible": false},
            { "mData": "idestado", "visible": false },
            { "mData": "latitud", "visible": false },
            { "mData": "longitud", "visible": false },
            { "mData": "tiempopedido" }
            
            
        ]
        } );

    //Con solo cargar la página remos por por los pedidos pendientes de entrega y salidos del horno.
    obtenerRutaMapa();
    obtenerPedidosEmpacadosDomicilio();
			
   
	
  
 });    

function obtenerRutaMapa()
{
    $.ajax({ 
                url: server + 'GetRutaMapaTienda', 
                dataType: 'json', 
                async: false, 
                success: 
                function(data){ 
                    rutaMapa = data.rutamapa;
                    latitudTienda = data.latitud;
                    longitudTienda = data.longitud;
                }
    });
}

//Método que invoca el servicio para obtener lista de municipios parametrizados en el sistema
function obtenerPedidosEmpacadosDomicilio(){

    $.ajax({ 
                url: server + 'obtenerPedidosEmpacadosDomicilio', 
                dataType: 'json', 
                async: false, 
                success: 
                function(data){ 
                        dtPedidos.clear().draw();
                        var latitud;
                        var longitud;
                        var map = new google.maps.Map($("#mapas").get(0), {
                            zoom: 7,
                            center: new google.maps.LatLng(latitudTienda, longitudTienda),
                            mapTypeId: google.maps.MapTypeId.ROADMAP
                        });
                        var ctaLayer = new google.maps.KmlLayer({
                            url: rutaMapa,
                            map: map,
                            scrollwheel: false,
                            zoom: 17
                        });
                        var infowindow = new google.maps.InfoWindow();
                        var marker, i;
                        for(var i = 0; i < data.length;i++){
                            var cadaPedido  = data[i];
                            dtPedidos.row.add(cadaPedido).draw();
                            latitud = cadaPedido.latitud;
                            longitud = cadaPedido.longitud;
                            marker = new google.maps.Marker({
                                position: new google.maps.LatLng(latitud, longitud),
                                map: map,
                                title: cadaPedido.idpedidotienda,
                                label: cadaPedido.idpedidotienda,
                                icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
                            });
                            google.maps.event.addListener(marker, 'click', (function(marker, i) {
                                return function() {
                                    infowindow.setContent("Dir Id " + cadaDirFuera.id);
                                    infowindow.open(map, marker);
                                }
                            })(marker, i));
                            //geocodeSinServicio(latitud, longitud, cadaPedido.idpedidotienda);
                            
                        }
                            
                } 
    });

}

function initMap() {
		

		
		var map = new google.maps.Map(document.getElementById("mapas"), {
          zoom: 13,
          scrollwheel: false,
          center: {lat: 6.264574, lng: -75.59654}
        });

       
      }




function geocodeSinServicio(lat, long, idPedido) 
{
    longitud = long;
    latitud = lat;
    var map = new google.maps.Map($("#mapas").get(0), {
		zoom: 7,
		center: new google.maps.LatLng(latitudTienda, longitudTienda),
		mapTypeId: google.maps.MapTypeId.ROADMAP
	});

    var ctaLayer = new google.maps.KmlLayer({
		url: rutaMapa,
		map: map,
		scrollwheel: false,
		zoom: 17
	});
    var infowindow = new google.maps.InfoWindow();
	var marker, i;
    marker = new google.maps.Marker({
	   	position: new google.maps.LatLng(latitud, longitud),
	   	map: map,
        title: idPedido,
        label: idPedido,
	   	icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
	});
}


