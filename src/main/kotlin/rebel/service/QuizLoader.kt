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


    val testImageActorCategory = Category("Name Actor", listOf(
        Question("resources/images/depp.png", "Johnny Depp", QuestionType.IMAGE, 100),
        Question("resources/images/leo.png", "Leonardo DiCaprio", QuestionType.IMAGE, 200),
        Question("resources/images/atkins.png", "Rowan Atkinson", QuestionType.IMAGE, 300),
    ))

    val testAudioSongsCategory = Category("Song and Artist", listOf(
        Question("resources/audio/gd_idiot.mp3", "Green Day - American Idiot", QuestionType.AUDIO, 100),
        Question("resources/audio/rs_paint.mp3", "The Rolling Stone - Paint It Black", QuestionType.AUDIO, 200),
        Question("resources/audio/ws_army.mp3", "The White Stripes - Seven Nation Army", QuestionType.AUDIO, 300),
    ))

    val testMovieCategory = Category("Movie", listOf(
        Question("resources/video/hp_stone.mp4", "Harry Potter and and the Philosopher's Stone", QuestionType.VIDEO, 100),
        Question("resources/video/legend.mp4", "I Am Legend", QuestionType.VIDEO, 200),
        Question("resources/video/rush_hour.mp4", "Rush Hour", QuestionType.VIDEO, 300),
    ))

    return Pack("Test Pack", listOf(
        testCapitalCategory,
        testImageActorCategory,
        testAudioSongsCategory,
        testMovieCategory
    ))
}