package rebel.service

val quizPacks: Map<String, Pack> = loadQuizPacks();

fun loadQuizPacks() : Map<String, Pack> {
    return mutableListOf(
        Pack("My first pack", listOf(
            Question("What is the capital of the United Kingdom", "London", QuestionType.TEXT,100)
        ))
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