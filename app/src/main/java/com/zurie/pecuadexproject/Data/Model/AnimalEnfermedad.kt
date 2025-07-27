package com.zurie.pecuadexproject.Data.Model

import java.util.Date

data class AnimalEnfermedad(
    var idAnimalEnfermedad: String = "",
    var animalId: Long,
    var enfermedadId: Long,
    var fechaDiagnostico: Date?,
    var observaciones: String?
)
