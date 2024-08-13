package rebel.service

val quizPacks: MutableMap<String, Pack> = loadQuizPacks();

fun loadQuizPacks() : MutableMap<String, Pack> {
    val rebelPack = rebelPack()
    return mutableMapOf(Pair(rebelPack.name, rebelPack()));
}

fun addPack(pack: Pack) {
    quizPacks[pack.name] = pack
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

fun rebelPack(): Pack {
    val flags = Category("Flags", listOf(
        Question("resources/images/rebel_pack/flags/uk.webp", "UK", QuestionType.IMAGE, 100),
        Question("resources/images/rebel_pack/flags/ua.jpg", "Ukraine", QuestionType.IMAGE, 200),
        Question("resources/images/rebel_pack/flags/la.png", "Latvia", QuestionType.IMAGE, 300),
        Question("resources/images/rebel_pack/flags/mon.jpg", "Monaco", QuestionType.IMAGE, 400),
        Question("resources/images/rebel_pack/flags/pa.webp", "Paraguay", QuestionType.IMAGE, 500)
    ))

    val capitals = Category("Capitals", listOf(
        Question("France", "Paris", QuestionType.TEXT, 100),
        Question("Austria", "Vienna", QuestionType.TEXT, 200),
        Question("Latvia", "Riga", QuestionType.TEXT, 300),
        Question("Armenia", "Yerevan", QuestionType.TEXT, 400),
        Question("Costa Rica", "San Jose", QuestionType.TEXT, 500)
    ))

    val movieByActors = Category("Movies By Actors", listOf(
        Question("Johnny Depp, Orlando Bloom, Keira Knightley", "Pirates of the Caribbean", QuestionType.TEXT, 100),
        Question("Leonardo DiCaprio, Kate Winslet, Billy Zane", "Titanic", QuestionType.TEXT, 200),
        Question("Timothée Chalamet, Zendaya, Oscar Isaac", "Dune", QuestionType.TEXT, 300),
        Question("Keanu Reeves, Shia LaBeouf, Peter Stormare", "Konstantin", QuestionType.TEXT, 400),
        Question("Eddie Redmayne, Katherine Waterston, Colin Farrell", "Fantastic Beasts and Where to Find Them", QuestionType.TEXT, 500),
    ))

    val actors = Category("Actors", listOf(
        Question("resources/images/rebel_pack/actors/tom.jpg", "Tom Cruise", QuestionType.IMAGE, 100),
        Question("resources/images/rebel_pack/actors/samuel.jpg", "Samuel L.Jackson", QuestionType.IMAGE, 200),
        Question("resources/images/rebel_pack/actors/anthony.jpg", "Antony Hopkins", QuestionType.IMAGE, 300),
        Question("resources/images/rebel_pack/actors/charlie.gif", "Charlie Sheen", QuestionType.IMAGE, 400),
        Question("resources/images/rebel_pack/actors/steve.webp", "Steve Buscemi", QuestionType.IMAGE, 500),
    ))

    val songs = Category("Song", listOf(
        Question("resources/audio/rebel_pack/tutu.mp3", "Toto - Africa", QuestionType.AUDIO, 100),
        Question("resources/audio/rebel_pack/dreams.mp3", "Eurythmics, Annie Lennox, Dave Stewart - Sweet Dreams", QuestionType.AUDIO, 200),
        Question("resources/audio/rebel_pack/army.mp3", "The White Stripes - Seven Nation Army", QuestionType.AUDIO, 300),
        Question("resources/audio/rebel_pack/tonight.mp3", "Eagle-Eye Cherry - Save Tonight", QuestionType.AUDIO, 400),
        Question("resources/audio/rebel_pack/plastic.mp3", "VIDEOCLUB - Amour Plastique", QuestionType.AUDIO, 500),
    ))

    val movies = Category("Movie", listOf(
        Question("resources/video/rebel_pack/hp_stone.mp4", "Harry Potter and and the Philosopher's Stone", QuestionType.VIDEO, 100),
        Question("resources/video/rebel_pack/lotr.mp4", "The Lord of the Rings: The Return of the King", QuestionType.VIDEO, 200),
        Question("resources/video/rebel_pack/tenet.mp4", "TENET", QuestionType.VIDEO, 300),
        Question("resources/video/rebel_pack/lighthouse.mp4", "The Lighthouse", QuestionType.VIDEO, 400),
        Question("resources/video/rebel_pack/platform.mp4", "The Platform", QuestionType.VIDEO, 500),
    ))

    return Pack("Rebel Pack", listOf(
        flags,
        capitals,
        movieByActors,
        actors,
        songs,
        movies
    ))
}