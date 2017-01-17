package de.htwg.se.durak

import com.google.inject.AbstractModule
import de.htwg.se.durak.model.{ PlayerFactory => PlayerFactoryTrait }
import de.htwg.se.durak.model.{ CardFactory => CardFactoryTrait }
import de.htwg.se.durak.model.{ AttackFactory => AttackFactoryTrait }
import de.htwg.se.durak.model.{ DeckFactory => DeckFactoryTrait }
import de.htwg.se.durak.model.impl._
import de.htwg.se.durak.controller.impl.GameRoundFactory
import de.htwg.se.durak.controller.GameRoundControllerFactory

class DurakModule extends AbstractModule {
  override def configure = {
    bind(classOf[GameRoundControllerFactory]).to(classOf[GameRoundFactory])
    bind(classOf[DeckFactoryTrait]).to(classOf[DeckFactory])
    bind(classOf[PlayerFactoryTrait]).to(classOf[PlayerFactory])
    bind(classOf[CardFactoryTrait]).to(classOf[CardFactory])
    bind(classOf[AttackFactoryTrait]).to(classOf[AttackFactory])
  }
}