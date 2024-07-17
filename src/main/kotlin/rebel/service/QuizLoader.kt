package rebel.service

val quizPacks: Map<String, Pack> = loadQuizPacks();

fun loadQuizPacks() : Map<String, Pack> {
    return mutableListOf(
        genTestPack()
    ).associateBy { it.name };
}

fun packs() : Collection<Pack> {
    return quizPacks.values;
}

fun packNames() : Set<String> {
    return quizPacks.keys;
}

fun packByName(packName: String) : Pack {
    return quizPacks[packName] ?: throw IllegalArgumentException("Pack $packName is not found");
}

fun genTestPack() : Pack {
    val testCapitalCategory = Category("Capitals", listOf(
        Question("What is the capital of Great Britain?", "London", QuestionType.TEXT, 100),
        Question("What is the capital of Ireland?", "Dublin", QuestionType.TEXT, 200),
        Question("What is the capital of France?", "Paris", QuestionType.TEXT, 300)
    ))

    val testActorCategory = Category("Movies By Actors", listOf(
        Question("Peter Stormare, Keanu Reeves, Shia LaBeouf", "Konstantin", QuestionType.TEXT, 100),
        Question("Daniel Radcliffe, Emma Watson, Rupert Grint", "Harry Potter", QuestionType.TEXT, 200),
        Question("Elijah Wood, Orlando Bloom, Viggo Mortensen", "The Lord of the Rings", QuestionType.TEXT, 300),
    ))

    return Pack("Test Pack", listOf(
        testCapitalCategory,
//        testActorCategory
    ))
}